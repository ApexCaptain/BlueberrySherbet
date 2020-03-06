package com.gmail.ayteneve93.blueberrysherbetcore.device

import android.bluetooth.*
import android.content.Context
import android.os.Build
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.*
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import io.reactivex.disposables.Disposable
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@Suppress("spellCheckingInspection")
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

    companion object {
        private val CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private val CCCF = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb")
        private val RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
        private val RX_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")
        private val TX_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
    }


    val rssi = ObservableField<Int>()
    val txPhy = ObservableField<Int>()
    val rxPhy = ObservableField<Int>()

    internal lateinit var mBluetoothDevice : BluetoothDevice
    private lateinit var mContext : Context

    private var mIsReliableWriteOnProcess = false
    private var mReliableWriteValue : String? = null

    protected lateinit var mBluetoothGatt : BluetoothGatt

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
            onServicesDiscovered()
        }

        override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
            this@BlueberryDevice.txPhy.set(txPhy)
            this@BlueberryDevice.rxPhy.set(rxPhy)
        }

        override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status)
            this@BlueberryDevice.txPhy.set(txPhy)
            this@BlueberryDevice.rxPhy.set(rxPhy)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            mCurrentRequestInfo?.onResponse(status, characteristic)
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
            if(mIsReliableWriteOnProcess) {
                if(characteristic != null && characteristic.getStringValue(0) == mReliableWriteValue) mBluetoothGatt.executeReliableWrite()
                else mBluetoothGatt.abortReliableWrite()
            }
            mCurrentRequestInfo?.onResponse(status, null)
            characteristic?.setValue("")
            if(!mIsReliableWriteOnProcess) {
                executeRequest()
                mIsBluetoothOnProgress = false
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.let { notifyCharacteristic ->
                mNotifyRequestList
                    .find { it.blueberryRequest.mUuid == notifyCharacteristic.uuid }
                    ?.onResponse(null, notifyCharacteristic)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)

        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            mIsReliableWriteOnProcess = false
            mReliableWriteValue = null
            mIsBluetoothOnProgress = false
            executeRequest()
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
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

                    BluetoothState.STATE_CONNECTING -> {
                        onDeviceConnecting()
                    }

                    BluetoothState.STATE_CONNECTED -> {
                        mBluetoothGatt.discoverServices()
                        mBluetoothGatt.readRemoteRssi()
                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) mBluetoothGatt.readPhy()
                        onDeviceConnected()
                    }

                    BluetoothState.STATE_DISCONNECTING -> {
                        dismissRssiUpdateInterval()
                        mIsServiceDiscovered = false
                        onDeviceDisconnecting()
                    }

                    BluetoothState.STATE_DISCONNECTED -> {
                        mBlueberryRequestInfoQueue.clear()
                        mNotifyRequestList.clear()
                        mCurrentRequestInfo = null
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
    open fun onServicesDiscovered() = BlueberryLogger.d("Services of ${mBluetoothDevice.address} are Discovered")



    /** Service Setting */
    private val mCharacteristicList = ArrayList<BluetoothGattCharacteristic>()
    private var mIsServiceDiscovered = false
    private var mIsBluetoothOnProgress = false

    private val mBlueberryRequestInfoQueue : PriorityQueue<BlueberryAbstractRequestInfo> = PriorityQueue { front, rear -> front.priority - rear.priority }
    private val mNotifyRequestList : ArrayList<BlueberryRequestInfoWithRepetitiousResults<out Any>> = ArrayList()
    private var mCurrentRequestInfo : BlueberryAbstractRequestInfo? = null

    internal fun enqueueBlueberryRequestInfo(blueberryRequestInfo: BlueberryAbstractRequestInfo) {
        if(mIsServiceDiscovered
            && mCharacteristicList.find { it.uuid == blueberryRequestInfo.uuid } == null) {
            BlueberryLogger.w("No Such Uuid Exists : '${blueberryRequestInfo.uuid}'")
        }
        mBlueberryRequestInfoQueue.offer(blueberryRequestInfo)
        if(mBlueberryRequestInfoQueue.size == 1) executeRequest()
    }

    internal fun cancelBlueberryRequest(blueberryRequestInfo: BlueberryAbstractRequestInfo) {
        if(blueberryRequestInfo.requestType in arrayOf(NOTIFY::class.java)) {
            mBlueberryRequestInfoQueue.offer(blueberryRequestInfo)
        } else {
            try {
                mBlueberryRequestInfoQueue.find { it.requestCode == blueberryRequestInfo.requestCode
                }?.let { mBlueberryRequestInfoQueue.remove(it) }
            } catch (exception : Exception) { exception.printStackTrace() }
        }
    }

    @Synchronized
    @Suppress("UNCHECKED_CAST")
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
                                if(currentRequestInfo.requestType == WRITE::class.java)
                                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                if(blueberryWriteRequestInfoWithoutResult.checkIsReliable) {
                                    mBluetoothGatt.beginReliableWrite()
                                    mIsReliableWriteOnProcess = true
                                    mReliableWriteValue = blueberryWriteRequestInfoWithoutResult.inputString
                                }
                                mBluetoothGatt.writeCharacteristic(characteristic)
                                blueberryWriteRequestInfoWithoutResult.isOnProgress = true
                                mIsBluetoothOnProgress = true
                            }

                            WRITE_WITHOUT_RESPONSE::class.java -> (currentRequestInfo as BlueberryRequestInfoWithNoResponse).let { blueberryWriteRequestInfoWithNoResponse ->
                                characteristic.setValue(blueberryWriteRequestInfoWithNoResponse.inputString)
                                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                                if(blueberryWriteRequestInfoWithNoResponse.checkIsReliable) {
                                    mBluetoothGatt.beginReliableWrite()
                                    mIsReliableWriteOnProcess = true
                                    mReliableWriteValue = blueberryWriteRequestInfoWithNoResponse.inputString
                                }

                                mBluetoothGatt.writeCharacteristic(characteristic)
                                blueberryWriteRequestInfoWithNoResponse.isOnProgress = true
                                mIsBluetoothOnProgress = true
                            }

                            READ::class.java -> {
                                mBluetoothGatt.readCharacteristic(characteristic)
                                currentRequestInfo.isOnProgress = true
                                mIsBluetoothOnProgress = true
                            }

                            NOTIFY::class.java, INDICATE::class.java -> (currentRequestInfo as BlueberryRequestInfoWithRepetitiousResults<Any>).let { blueberryRequestInfoWithRepetitiousResults ->
                                mBluetoothGatt.setCharacteristicNotification(characteristic, blueberryRequestInfoWithRepetitiousResults.isNotificationEnabled)
                                val descriptor = characteristic.getDescriptor(CCCD)
                                /** ToDo : Notify Limit 확인 */
                                if(descriptor != null) {
                                    if(blueberryRequestInfoWithRepetitiousResults.isNotificationEnabled) {
                                        descriptor.value =
                                            if(currentRequestInfo.requestType == NOTIFY::class.java) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                            else BluetoothGattDescriptor.ENABLE_INDICATION_VALUE

                                        if(mNotifyRequestList.find { it.uuid == characteristic.uuid } != null) {
                                            mNotifyRequestList.removeIf { it.uuid == characteristic.uuid }
                                            mNotifyRequestList.add(blueberryRequestInfoWithRepetitiousResults)
                                            return
                                        }
                                        mNotifyRequestList.add(blueberryRequestInfoWithRepetitiousResults)
                                    } else {
                                        descriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                                        mNotifyRequestList.removeIf { it.requestCode == blueberryRequestInfoWithRepetitiousResults.requestCode }
                                    }
                                    mBluetoothGatt.writeDescriptor(descriptor)
                                } else disconnect()
                            }



                            else -> throw IllegalAccessException("Blueberry Execution Access by Unknown Request Type")
                        }
                        currentRequestInfo.startTimer()
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