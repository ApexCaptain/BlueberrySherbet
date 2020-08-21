package com.gmail.ayteneve93.blueberrysherbetcore.device

import android.bluetooth.*
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryAbstractRequest
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestWithNoResponse
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestWithRepetitiousResults
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestWithoutResult
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
    protected abstract fun setServiceImpl() : BlueberryService

    companion object {
        private val CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private val CCCF = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb")
        private val RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
        private val RX_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")
        private val TX_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")

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

        // https://medium.com/@abrisad_it/ble-error-codes-a3c6675b29c1
        enum class BleStatus(val statusCode : Short) {
            BLE_HCI_STATUS_CODE_SUCCESS(0x00),
            BLE_HCI_STATUS_CODE_UNKNWON_BTLE_COMMAND(0x01),
            BLE_HCI_STATUS_CODE_UNKNOWN_CONNECTION_IDENTIFIER(0x02),
            BLE_HCI_AUTHENTICATION_FAILURE(0x05),
            BLE_HCI_STATUS_CODE_PIN_OR_KEY_MISSING(0x06),
            BLE_HCI_MEMORY_CAPACITY_EXCEEDED(0x07),
            BLE_HCI_CONNECTION_TIMEOUT(0x08),
            BLE_HCI_STATUS_CODE_COMMAND_DISALLOWED(0x0C),
            BLE_HCI_STATUS_CODE_INVALID_BTLE_COMMAND_PARAMETERS(0x12),
            BLE_HCI_REMOTE_USER_TERMINATED_CONNECTION(0x13),
            BLE_HCI_REMOTE_DEV_TERMINATION_DUE_TO_LOW_RESOURCES(0x14),
            BLE_HCI_REMOTE_DEV_TERMINATION_DUE_TO_POWER_OFF(0x15),
            BLE_HCI_LOCAL_HOST_TERMINATED_CONNECTION(0x16),
            BLE_HCI_UNSUPPORTED_REMOTE_FEATURE(0x1A),
            BLE_HCI_STATUS_CODE_INVALID_LMP_PARAMETERS(0x1E),
            BLE_HCI_STATUS_CODE_UNSPECIFIED_ERROR(0x1F),
            BLE_HCI_STATUS_CODE_LMP_RESPONSE_TIMEOUT(0x22),
            BLE_HCI_STATUS_CODE_LMP_PDU_NOT_ALLOWED(0x24),
            BLE_HCI_INSTANT_PASSED(0x28),
            BLE_HCI_PAIRING_WITH_UNIT_KEY_SUPPORTED(0x29),
            BLE_HCI_DIFFERENT_TRANSATION_COLLISION(0x2A),
            BLE_HCI_CONTROLLER_BUSY(0x3A),
            BLE_HCI_CONN_INTERVAL_UNACCEPTABLE(0x3B),
            BLE_HCI_DIRECTED_ADVERTISER_TIMEOUT(0x3C),
            BLE_HCI_CONN_TERMINATED_DUE_TO_MIC_FAILURE(0x3D),
            BLE_HCI_CONN_FAILED_TO_BE_ESTABLISHED(0x3E),
            GATT_NO_RESOURCES(0x80),
            GATT_INTERNAL_ERROR(0x81),
            GATT_WRONG_STATE(0x82),
            GATT_DB_FULL(0x83),
            GATT_BUSY(0x84),
            GATT_ERROR(0x85),
            GATT_CMD_STARTED(0x86),
            GATT_ILLEGAL_PARAMETER(0x87),
            GATT_AUTH_FAIL(0x89),
            GATT_MORE(0x8a),
            GATT_INVALID_CFG(0x8b),
            GATT_SERVICE_STARTED(0x8C),
            GATT_ENCRYPTED_NO_MITM(0x8D),
            GATT_NOT_ENCRYPTED(0x8E),
            GATT_CONGESTED(0x8F),
            GATT_CCC_CFG_ERR(0xFD),
            GATT_PRC_IN_PROGRESS(0xFE),
            GATT_OUT_OF_RANGE(0xFF);
            companion object {
                fun getStatusFromCode(statusCode : Short) : BleStatus? = values().find { it.statusCode == statusCode }
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
            mCurrentRequest?.onResponse(status, characteristic)
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
            mCurrentRequest?.onResponse(status, null)
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
                    .find { it.mBlueberryRequestInfo.mUuid == notifyOrIndicateCharacteristic.uuid }
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
                    .find { it.mBlueberryRequestInfo.mUuid == notifyOrIndicateCharacteristic.uuid }
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
                        // ToDo : MTU 값 변경 Method 호출시 WRITE -> RESPONSE 메소드 먹통 되는 Issue 추후 해결...
                        // requestMtu(255)
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
                        mBlueberryRequestQueue.clear()
                        mNotifyOrIndicateRequestList.clear()
                        mCurrentRequest = null
                        onDeviceDisconnected()
                    }

                }
            }
        })
    }

    /** Device Life Cycle Callback */
    protected open fun onDeviceDisconnected() {
        BlueberryLogger.d("Disconnected from ${mBluetoothDevice.address}")
    }

    protected open fun onDeviceConnecting() {
        BlueberryLogger.d("Connecting to ${mBluetoothDevice.address}")
    }

    protected open fun onDeviceConnected() {
        BlueberryLogger.d("Connected to ${mBluetoothDevice.address}")
    }

    protected open fun onDeviceDisconnecting() {
        BlueberryLogger.d("Disconnecting from ${mBluetoothDevice.address}")
    }

    protected open fun onServicesDiscovered() {
        BlueberryLogger.d("Services of ${mBluetoothDevice.address} are Discovered")
    }


    /** Data Converter */
    val blueberryConverterPrev : BlueberryConverterPrev = BlueberryConverterPrev()


    /** Service Setting */
    private val mCharacteristicList = ArrayList<BluetoothGattCharacteristic>()
    private var mIsServiceDiscovered = false
    private var mIsBluetoothOnProgress = false

    private val mBlueberryRequestQueue : PriorityQueue<BlueberryAbstractRequest> = PriorityQueue()
    private val mNotifyOrIndicateRequestList : ArrayList<BlueberryRequestWithRepetitiousResults<out Any>> = ArrayList()
    private var mCurrentRequest : BlueberryAbstractRequest? = null

    internal fun enqueueBlueberryRequestInfo(blueberryRequest: BlueberryAbstractRequest) {
        if(mIsServiceDiscovered && mCharacteristicList.find { it.uuid == blueberryRequest.mUuid } == null)
            BlueberryLogger.w("No Such Uuid Exists : '${blueberryRequest.mUuid}'")
        else {
            mBlueberryRequestQueue.offer(blueberryRequest)
            if(mBlueberryRequestQueue.size == 1) {
                executeRequest()
            }
        }
    }

    internal fun cancelBlueberryRequest(blueberryRequest: BlueberryAbstractRequest) {
        if(blueberryRequest.mRequestType in arrayOf(NOTIFY::class.java, INDICATE::class.java)) mBlueberryRequestQueue.offer(blueberryRequest)
        else {
            try {
                mBlueberryRequestQueue.find { it.mRequestCode == blueberryRequest.mRequestCode
                }?.let { mBlueberryRequestQueue.remove(it) }
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

                mBlueberryRequestQueue.isNotEmpty() -> {
                    mCurrentRequest = mBlueberryRequestQueue.poll()
                    mCurrentRequest?.let { currentRequestInfo ->
                        mCharacteristicList.find { it.uuid == currentRequestInfo.mUuid }
                            ?.let { characteristic ->

                                when(currentRequestInfo.mRequestType) {

                                    WRITE::class.java -> (currentRequestInfo as BlueberryRequestWithoutResult).let { blueberryWriteRequestInfoWithoutResult ->

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
                                        BlueberryLogger.v("WRITE method is called. Sent data : ${blueberryWriteRequestInfoWithoutResult.inputString}")
                                    }

                                    WRITE_WITHOUT_RESPONSE::class.java -> (currentRequestInfo as BlueberryRequestWithNoResponse).let { blueberryWriteRequestInfoWithNoResponse ->
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

                                    NOTIFY::class.java, INDICATE::class.java -> (currentRequestInfo as BlueberryRequestWithRepetitiousResults<Any>).let { blueberryRequestInfoWithRepetitiousResults ->
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
    /*protected*/ private fun requestMtu(mtu : Int) {
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