package com.gmail.ayteneve93.blueberrysherbetcore.device

import android.bluetooth.*
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryAbstractRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestInfoWithNoResponse
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestInfoWithRepetitiousResults
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestInfoWithoutResult
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import io.reactivex.disposables.Disposable
import java.lang.ClassCastException
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Abstract class of BLE device.
 *
 * You can configure specific options for each BLE device by inheriting this abstract class.
 *
 */
@Suppress("spellCheckingInspection")
abstract class BlueberryDevice<BlueberryService> protected constructor() {

    var autoConnect: Boolean = true
    val bluetoothState = ObservableField(BluetoothState.STATE_DISCONNECTED)
    val blueberryService : BlueberryService by lazy { setServiceImpl() }
    abstract fun setServiceImpl() : BlueberryService

    companion object {
        private val CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private val CCCF = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb")
        private val RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
        private val RX_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")
        private val TX_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")

        /**
         * B
         */
        enum class BlueberryConnectionPriority(val value : Int) {
            CONNECTION_PRIORITY_BALANCED(BluetoothGatt.CONNECTION_PRIORITY_BALANCED),
            CONNECTION_PRIORITY_HIGH(BluetoothGatt.CONNECTION_PRIORITY_HIGH),
            CONNECTION_PRIORITY_LOW_POWER(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER)
        }

        enum class BluetoothState(val stateCode : Int) {
            STATE_DISCONNECTED(0x00000000),
            STATE_CONNECTING(0x00000001),
            STATE_CONNECTED(0x00000002),
            STATE_DISCONNECTING(0x00000003);
            companion object {
                internal fun getStateFromCode(stateCode : Int) : BluetoothState = values().find { it.stateCode == stateCode }!!
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        enum class PhyOption(val value : Int) {
            PHY_OPTION_NO_PREFERRED(BluetoothDevice.PHY_OPTION_NO_PREFERRED),
            PHY_OPTION_S2(BluetoothDevice.PHY_OPTION_S2),
            PHY_OPTION_S8(BluetoothDevice.PHY_OPTION_S8)
        }
    }


    val rssiBinding = ObservableField<Int>()
    val txPhyBinding = ObservableField<Int>()
    val rxPhyBinding = ObservableField<Int>()
    val mtuBinding = ObservableField<Int>()

    internal lateinit var mBluetoothDevice : BluetoothDevice
    private lateinit var mContext : Context

    private var mIsReliableWriteOnProcess = false
    private var mReliableWriteValue : String? = null

    protected lateinit var mBluetoothGatt : BluetoothGatt

    private var mBluetoothGattCallback = object : BluetoothGattCallback() {

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            this@BlueberryDevice.rssiBinding.set(rssi)
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            bluetoothState.set(BluetoothState.getStateFromCode(newState))
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.let { bluetoothGatt ->
                mCharacteristicList.clear()
                bluetoothGatt.services.forEach { eachGattService -> mCharacteristicList.addAll(eachGattService.characteristics) }
            }
            mIsServiceDiscovered = true
            executeRequest()
            onServicesDiscovered()
        }

        override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
            this@BlueberryDevice.txPhyBinding.set(txPhy)
            this@BlueberryDevice.rxPhyBinding.set(rxPhy)
            mIsBluetoothOnProgress = false
            executeRequest()
        }

        override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status)
            this@BlueberryDevice.txPhyBinding.set(txPhy)
            this@BlueberryDevice.rxPhyBinding.set(rxPhy)
            mIsBluetoothOnProgress = false
            executeRequest()
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            this@BlueberryDevice.mtuBinding.set(mtu)
            mIsBluetoothOnProgress = false
            executeRequest()
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
                mIsBluetoothOnProgress = false
                executeRequest()
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.let { notifyOrIndicateCharacteristic ->
                mNotifyOrIndicateRequestList
                    .find { it.mBlueberryRequest.mUuid == notifyOrIndicateCharacteristic.uuid }
                    ?.onResponse(0, notifyOrIndicateCharacteristic)
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            descriptor?.characteristic?.let { notifyOrIndicateCharacteristic ->
                mNotifyOrIndicateRequestList
                    .find { it.mBlueberryRequest.mUuid == notifyOrIndicateCharacteristic.uuid }
                    ?.onResponse(null, notifyOrIndicateCharacteristic)
                mIsBluetoothOnProgress = false
                executeRequest()
            }
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            mIsReliableWriteOnProcess = false
            mReliableWriteValue = null
            mIsBluetoothOnProgress = false
            executeRequest()
        }

    }
    internal fun initialize(bluetoothDevice: BluetoothDevice, context : Context, autoConnect : Boolean) {
        this.mBluetoothDevice = bluetoothDevice
        mContext = context
        this.autoConnect = autoConnect
        setDefaultConnectionStateChangeDelegate()
        setDefaultRssiValueChangeDelegate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) setDefaultPhyValueChangeDelegate()
        setDefaultMtuValueChangeDelegate()
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
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) this@BlueberryDevice.readPhy()
                        requestMtu(513)
                        onDeviceConnected()
                    }

                    BluetoothState.STATE_DISCONNECTING -> {
                        dismissRssiUpdateInterval()
                        rssiBinding.set(null)
                        txPhyBinding.set(null)
                        rxPhyBinding.set(null)
                        mtuBinding.set(null)
                        mIsServiceDiscovered = false
                        onDeviceDisconnecting()
                    }

                    BluetoothState.STATE_DISCONNECTED -> {
                        mMtuRequestInfoQueue.clear()
                        mPhyRequestInfoQueue.clear()
                        mBlueberryRequestInfoQueue.clear()
                        mNotifyOrIndicateRequestList.clear()
                        mCurrentRequestInfo = null
                        onDeviceDisconnected()
                    }

                }
            }
        })
    }

    /** Device Life Cycle Callback */
    open fun onDeviceDisconnected()  = BlueberryLogger.d("Disconnected from ${mBluetoothDevice.address}")
    open fun onDeviceConnecting()    = BlueberryLogger.d("Connecting to ${mBluetoothDevice.address}")
    open fun onDeviceConnected()     = BlueberryLogger.d("Connected to ${mBluetoothDevice.address}")
    open fun onDeviceDisconnecting() = BlueberryLogger.d("Disconnecting from ${mBluetoothDevice.address}")
    open fun onServicesDiscovered()  = BlueberryLogger.d("Services of ${mBluetoothDevice.address} are Discovered")

    /** Service Setting */
    private val mCharacteristicList = ArrayList<BluetoothGattCharacteristic>()
    private var mIsServiceDiscovered = false
    private var mIsBluetoothOnProgress = false

    private val mBlueberryRequestInfoQueue : PriorityQueue<BlueberryAbstractRequestInfo> = PriorityQueue()
    private val mNotifyOrIndicateRequestList : ArrayList<BlueberryRequestInfoWithRepetitiousResults<out Any>> = ArrayList()
    private var mCurrentRequestInfo : BlueberryAbstractRequestInfo? = null

    internal fun enqueueBlueberryRequestInfo(blueberryRequestInfo: BlueberryAbstractRequestInfo) {
        if(mIsServiceDiscovered && mCharacteristicList.find { it.uuid == blueberryRequestInfo.mUuid } == null)
            BlueberryLogger.w("No Such Uuid Exists : '${blueberryRequestInfo.mUuid}'")
        else {
            mBlueberryRequestInfoQueue.offer(blueberryRequestInfo)
            if(mBlueberryRequestInfoQueue.size == 1) executeRequest()
        }
    }

    internal fun cancelBlueberryRequest(blueberryRequestInfo: BlueberryAbstractRequestInfo) {
        if(blueberryRequestInfo.mRequestType in arrayOf(NOTIFY::class.java, INDICATE::class.java)) mBlueberryRequestInfoQueue.offer(blueberryRequestInfo)
        else {
            try {
                mBlueberryRequestInfoQueue.find { it.mRequestCode == blueberryRequestInfo.mRequestCode
                }?.let { mBlueberryRequestInfoQueue.remove(it) }
            } catch (exception : Exception) { exception.printStackTrace() }
        }
    }

    @Synchronized
    @Suppress("UNCHECKED_CAST")
    private fun executeRequest() {
        if(::mBluetoothGatt.isInitialized
            && mIsServiceDiscovered
            && bluetoothState.get() == BluetoothState.STATE_CONNECTED
            && !mIsBluetoothOnProgress) {
            when {
                mMtuRequestInfoQueue.isNotEmpty() -> {
                    mMtuRequestInfoQueue.poll()?.let { mtuRequestInfo ->
                        mBluetoothGatt.requestMtu(mtuRequestInfo.mtu)
                        mIsBluetoothOnProgress = true
                    }
                }
                mBlueberryRequestInfoQueue.isNotEmpty() -> {
                    mCurrentRequestInfo = mBlueberryRequestInfoQueue.poll()
                    mCurrentRequestInfo?.let { currentRequestInfo ->
                        mCharacteristicList.find { it.uuid == currentRequestInfo.mUuid }
                            ?.let { characteristic ->

                                when(currentRequestInfo.mRequestType) {


                                    WRITE::class.java -> (currentRequestInfo as BlueberryRequestInfoWithoutResult).let { blueberryWriteRequestInfoWithoutResult ->
                                        characteristic.setValue(blueberryWriteRequestInfoWithoutResult.inputString)
                                        if(currentRequestInfo.mRequestType == WRITE::class.java)
                                            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                        if(blueberryWriteRequestInfoWithoutResult.checkIsReliable) {
                                            mBluetoothGatt.beginReliableWrite()
                                            mIsReliableWriteOnProcess = true
                                            mReliableWriteValue = blueberryWriteRequestInfoWithoutResult.inputString
                                        }
                                        mBluetoothGatt.writeCharacteristic(characteristic)
                                        blueberryWriteRequestInfoWithoutResult.mIsOnProgress = true
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
                                        blueberryWriteRequestInfoWithNoResponse.mIsOnProgress = true
                                        mIsBluetoothOnProgress = true
                                    }

                                    READ::class.java -> {
                                        mBluetoothGatt.readCharacteristic(characteristic)
                                        currentRequestInfo.mIsOnProgress = true
                                        mIsBluetoothOnProgress = true
                                    }

                                    NOTIFY::class.java, INDICATE::class.java -> (currentRequestInfo as BlueberryRequestInfoWithRepetitiousResults<Any>).let { blueberryRequestInfoWithRepetitiousResults ->
                                        mBluetoothGatt.setCharacteristicNotification(characteristic, blueberryRequestInfoWithRepetitiousResults.isNotificationEnabled)
                                        val descriptor = characteristic.getDescriptor(CCCD)
                                        /** ToDo : Notify Limit 확인 */
                                        if(descriptor != null) {
                                            if(blueberryRequestInfoWithRepetitiousResults.isNotificationEnabled) {
                                                descriptor.value =
                                                    if(currentRequestInfo.mRequestType == NOTIFY::class.java) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                                    else BluetoothGattDescriptor.ENABLE_INDICATION_VALUE

                                                if(mNotifyOrIndicateRequestList.find { it.mUuid == characteristic.uuid } != null) {
                                                    mNotifyOrIndicateRequestList.removeIf { it.mUuid == characteristic.uuid }
                                                    mNotifyOrIndicateRequestList.add(blueberryRequestInfoWithRepetitiousResults)
                                                    return
                                                }
                                                mNotifyOrIndicateRequestList.add(blueberryRequestInfoWithRepetitiousResults)
                                            } else {
                                                descriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                                                mNotifyOrIndicateRequestList.removeIf { it.mRequestCode == blueberryRequestInfoWithRepetitiousResults.mRequestCode }
                                            }
                                            mBluetoothGatt.writeDescriptor(descriptor)

                                            mBluetoothGatt.readDescriptor(descriptor)

                                            blueberryRequestInfoWithRepetitiousResults.mIsOnProgress = true
                                            mIsBluetoothOnProgress = true
                                        } else disconnect()
                                    }
                                    else -> throw IllegalAccessException("Blueberry Execution Access by Unknown Request Type")
                                }
                                currentRequestInfo.startTimer()
                            }
                    }
                }
                mPhyRequestInfoQueue.isNotEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    mPhyRequestInfoQueue.poll()?.let { phyRequestInfo ->
                        if(phyRequestInfo.useReadOperationOnly) mBluetoothGatt.readPhy()
                        else if(
                            phyRequestInfo.txPhy != null
                            && phyRequestInfo.rxPhy != null
                            && phyRequestInfo.phyOption != null) mBluetoothGatt.setPreferredPhy(
                            phyRequestInfo.txPhy,
                            phyRequestInfo.rxPhy,
                            phyRequestInfo.phyOption.value
                        )
                        mIsBluetoothOnProgress = true
                    }
                }
            }

        }
    }
    /** Phy Value Change Delegate */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDefaultPhyValueChangeDelegate() {
        arrayOf(txPhyBinding, rxPhyBinding).forEach {
            it.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if(txPhyBinding.get() != null && rxPhyBinding.get() != null) {
                        onPhyValueChanged(txPhyBinding.get()!!, rxPhyBinding.get()!!)
                    }
                }
            })
        }
    }

    open fun onPhyValueChanged(txPhy: Int, rxPhy: Int) = BlueberryLogger.d("Phy Value Changed. txPhy : $txPhy, rxPhy : $rxPhy at ${mBluetoothDevice.address}")
    private data class PhyRequestInfo(
        val useReadOperationOnly : Boolean = true,
        val txPhy : Int? = null,
        val rxPhy : Int? = null,
        val phyOption : PhyOption? = null
    )
    private val mPhyRequestInfoQueue : Queue<PhyRequestInfo> = LinkedList()
    @RequiresApi(Build.VERSION_CODES.O)
    protected fun readPhy() {
        mPhyRequestInfoQueue.offer(PhyRequestInfo())
        executeRequest()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    protected fun setPreferredPhy(txPhy: Int, rxPhy: Int) {
        mPhyRequestInfoQueue.offer(PhyRequestInfo(false, txPhy, rxPhy))
        executeRequest()
    }

    /** Rssi Value Change Delegate */
    private fun setDefaultRssiValueChangeDelegate() {
        rssiBinding.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                rssiBinding.get()?.let { onRssiValueChanged(it) }
            }
        })
    }
    open fun onRssiValueChanged(rssiValue : Int) = BlueberryLogger.d("Rssi Value Changed to $rssiValue at ${mBluetoothDevice.address}")
    private lateinit var mRssiUpdateIntervalDisposable: Disposable
    fun setRssiUpdateInterval(intervalTime : Long, intervalTimeUnit: TimeUnit) {
        if(bluetoothState.get() == BluetoothState.STATE_CONNECTED) {
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
    }
    fun dismissRssiUpdateInterval() { if(::mRssiUpdateIntervalDisposable.isInitialized && !mRssiUpdateIntervalDisposable.isDisposed) mRssiUpdateIntervalDisposable.dispose() }

    /** Mtu Value Change Delegate */
    private fun setDefaultMtuValueChangeDelegate() {
        mtuBinding.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                mtuBinding.get()?.let { onMtuValueChanged(it) }
            }
        })
    }
    open fun onMtuValueChanged(mtu : Int) = BlueberryLogger.d("Mtu Value Changed to $mtu at ${mBluetoothDevice.address}")
    private data class MtuRequestInfo(val mtu : Int)
    private val mMtuRequestInfoQueue : Queue<MtuRequestInfo> = LinkedList()
    protected fun requestMtu(mtu : Int) {
        mMtuRequestInfoQueue.offer(MtuRequestInfo(mtu))
        executeRequest()
    }

    /** Change Connection Priority */
    fun setConnectionPriority(connectionPriority: BlueberryConnectionPriority) {
        mBluetoothGatt.requestConnectionPriority(connectionPriority.value)
    }

    /** Basic Connection Controlling Operation */
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

    /** Override Functions */
    @Suppress("UNCHECKED_CAST")
    override fun equals(other: Any?): Boolean = when(other) {
        null -> false
        is BlueberryDevice<*> -> {
            try {
                (other as BlueberryDevice<BlueberryService>)
                    .mBluetoothDevice.address == this.mBluetoothDevice.address
            } catch(exception : ClassCastException) { false }
        }
        else -> false
    }


    override fun hashCode(): Int = (31 + blueberryService.hashCode()) * 31 + mBluetoothDevice.hashCode()

    override fun toString(): String {
        return super.toString()
    }
}