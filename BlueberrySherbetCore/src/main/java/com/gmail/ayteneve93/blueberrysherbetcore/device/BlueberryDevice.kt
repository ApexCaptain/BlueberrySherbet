package com.gmail.ayteneve93.blueberrysherbetcore.device

import android.bluetooth.*
import android.content.Context
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE_WITHOUT_RESPONSE
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryRequest
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryRequestInfoWithoutResult
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequest
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BlueberryDevice<BlueberryService> {

    var autoConnect: Boolean = true
    val bluetoothState = ObservableField(BluetoothState.STATE_DISCONNECTED)
    val blueberryService : BlueberryService by lazy { setServiceImpl() }
    abstract fun setServiceImpl() : BlueberryService

    enum class BluetoothState(val stateCode : Int) {
        STATE_DISCONNECTED(0x00000000),
        STATE_CONNECTING(0x00000001),
        STATE_CONNECTED(0x00000002),
        STATE_DISCONNECTING(0x00000003);
        companion object {
            internal fun getStateFromCode(stateCode : Int) : BluetoothState = values().find { it.stateCode == stateCode }!!
        }
    }
    val rssi = ObservableField<Int>()
    private lateinit var mBluetoothDevice : BluetoothDevice
    private lateinit var mContext : Context
    private lateinit var mBluetoothGatt : BluetoothGatt

    private var mBluetoothGattCallback = object : BluetoothGattCallback() {

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            this@BlueberryDevice.rssi.set(rssi)
        }
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            bluetoothState.set(BluetoothState.getStateFromCode(newState))
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.let { bluetoothGatt ->
                mCharacteristicList.clear()
                bluetoothGatt.services.forEach { eachGattService ->
                    mCharacteristicList.addAll(eachGattService.characteristics)
                }
            }
            mIsServiceDiscovered = true
            executeRequest()
        }

        override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            mCurrentRequestInfo?.onResponse(status, characteristic?.getStringValue(0))
            characteristic?.setValue("")
            mIsBluetoothOnProgress = false
            executeRequest()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            mCurrentRequestInfo?.onResponse(status, null)
            characteristic?.setValue("")
            mIsBluetoothOnProgress = false
            executeRequest()
        }



        override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status)
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
        }

    }
    internal fun initialize(bluetoothDevice: BluetoothDevice, context : Context, autoConnect : Boolean) {
        this.mBluetoothDevice = bluetoothDevice
        mContext = context
        this.autoConnect = autoConnect
        setDefaultConnectionStateChangeDelegate()
        setDefaultRssiValueChangeDelegate()
        connect()
    }

    /** Connection State Change Delegate */
    private fun setDefaultConnectionStateChangeDelegate() {
        bluetoothState.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                when(bluetoothState.get()) {

                    BluetoothState.STATE_CONNECTING -> { onDeviceConnecting() }

                    BluetoothState.STATE_CONNECTED -> {
                        mBluetoothGatt.discoverServices()
                        mBluetoothGatt.readRemoteRssi()
                        onDeviceConnected()
                    }

                    BluetoothState.STATE_DISCONNECTING -> {
                        dismissRssiUpdateInterval()
                        onDeviceDisconnecting()
                        mIsServiceDiscovered = false
                    }

                    BluetoothState.STATE_DISCONNECTED -> {
                        mBlueberryRequestInfoQueue.clear()
                        onDeviceDisconnected()
                    }

                }
            }
        })
    }
    open fun onDeviceDisconnected()  = BlueberryLogger.d("Disconnected from ${mBluetoothDevice.address}")
    open fun onDeviceConnecting()    = BlueberryLogger.d("Connecting to ${mBluetoothDevice.address}")
    open fun onDeviceConnected()     = BlueberryLogger.d("Connected to ${mBluetoothDevice.address}")
    open fun onDeviceDisconnecting() = BlueberryLogger.d("Disconnecting from ${mBluetoothDevice.address}")



    /** Service Setting */
    private val mCharacteristicList = ArrayList<BluetoothGattCharacteristic>()
    private var mIsServiceDiscovered = false
    private var mIsBluetoothOnProgress = false

    private val mBlueberryRequestInfoQueue : PriorityQueue<BlueberryRequestInfo> = PriorityQueue { front, rear -> front.priority - rear.priority }
    private var mCurrentRequestInfo : BlueberryRequestInfo? = null

    internal fun enqueueBlueberryRequestInfo(blueberryRequestInfo: BlueberryRequestInfo) {
        if(mIsServiceDiscovered
            && mCharacteristicList.find { it.uuid == blueberryRequestInfo.uuid } == null) {
            BlueberryLogger.w("No Such Uuid Exists : '${blueberryRequestInfo.uuid}'")
        }
        mBlueberryRequestInfoQueue.offer(blueberryRequestInfo)
        if(mBlueberryRequestInfoQueue.size == 1) executeRequest()
    }

    @Synchronized
    private fun executeRequest() {
        if(mIsServiceDiscovered
            && bluetoothState.get() == BluetoothState.STATE_CONNECTED
            && !mIsBluetoothOnProgress
            && mBlueberryRequestInfoQueue.isNotEmpty()) {
            mCurrentRequestInfo = mBlueberryRequestInfoQueue.poll()
            mCurrentRequestInfo?.let { currentRequestInfo ->
                mCharacteristicList.find { it.uuid == currentRequestInfo.uuid }
                    ?.let { characteristic ->
                        when(currentRequestInfo.requestType) {


                            WRITE::class.java -> (currentRequestInfo as BlueberryRequestInfoWithoutResult).let { blueberryWriteRequestInfoWithoutResult ->
                                characteristic.setValue(blueberryWriteRequestInfoWithoutResult.inputString)
                                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                mBluetoothGatt.writeCharacteristic(characteristic)
                                blueberryWriteRequestInfoWithoutResult.isOnProgress = true
                                mIsBluetoothOnProgress = true
                            }


                            else -> throw IllegalAccessException("Blueberry Execution Access by Unknown Request Type")
                        }
                    }
            }
        }
    }

    /** Rssi Value Change Delegate */
    private fun setDefaultRssiValueChangeDelegate() {
        rssi.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                rssi.get()?.let { onRssiValueChanged(it) }
            }
        })
    }
    open fun onRssiValueChanged(rssiValue : Int) = BlueberryLogger.d("Rssi Value Changed to $rssiValue at ${mBluetoothDevice.address}")
    private lateinit var mRssiUpdateIntervalDisposable: Disposable
    fun setRssiUpdateInterval(intervalTime : Long, intervalTimeUnit: TimeUnit) {
        dismissRssiUpdateInterval()
        var sIntervalTime = intervalTime
        var sIntervalTimeUnit = intervalTimeUnit
        if(intervalTimeUnit.toSeconds(intervalTime) < 10) {
            sIntervalTime = 10
            sIntervalTimeUnit = TimeUnit.SECONDS
        }
        mRssiUpdateIntervalDisposable = io.reactivex.Observable.interval(0, sIntervalTime, sIntervalTimeUnit)
            .subscribe { if(bluetoothState.get() == BluetoothState.STATE_CONNECTED) mBluetoothGatt.readRemoteRssi() }
    }
    fun dismissRssiUpdateInterval() { if(::mRssiUpdateIntervalDisposable.isInitialized && !mRssiUpdateIntervalDisposable.isDisposed) mRssiUpdateIntervalDisposable.dispose() }


    fun connect() {
        if(bluetoothState.get() == BluetoothState.STATE_DISCONNECTED) {
            bluetoothState.set(BluetoothState.STATE_CONNECTING)
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, autoConnect, mBluetoothGattCallback)
        }
    }
    fun disconnect() {
        if(bluetoothState.get() == BluetoothState.STATE_CONNECTED) {
            bluetoothState.set(BluetoothState.STATE_DISCONNECTING)
            mBluetoothGatt.disconnect()
        }
    }


}