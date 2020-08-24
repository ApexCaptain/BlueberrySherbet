package com.gmail.ayteneve93.blueberrysherbetcore.request.call

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetannotations.INDICATE
import com.gmail.ayteneve93.blueberrysherbetannotations.NOTIFY
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import io.reactivex.Observable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class BlueberryRequestWithRepetitiousResults<ReturnType>(
    uuid : UUID,
    priority : Int,
    awaitingMills : Int,
    blueberryRequestInfo : BlueberryAbstractRequestInfo<ReturnType>,
    requestType : Class<out Annotation>,
    private val endSignal : String
) : BlueberryAbstractRequest(
    mUuid = uuid,
    mPriority = priority,
    mAwaitingMills = awaitingMills,
    mBlueberryRequestInfo = blueberryRequestInfo as BlueberryAbstractRequestInfo<out Any>,
    mRequestType = requestType) {

    internal var isNotificationEnabled = true
    private lateinit var callback : BlueberryCallbackWithResult<ReturnType>

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mBlueberryRequestInfo.mReturnTypeClass.typeName
        else mBlueberryRequestInfo.mReturnTypeClass.simpleName
        this["End Signal"] = endSignal
    }

    override fun cancel() {
        isNotificationEnabled = false
        super.cancel()
    }

    fun enqueue(callback : BlueberryCallbackWithResult<ReturnType>) {
        this.callback = callback
        mBlueberryRequestInfo.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }

    fun byRx2() : Observable<BlueberryCallbackResultData<ReturnType>>
            = Observable.create { emitter ->
        enqueue { status, value -> emitter.onNext(
            BlueberryCallbackResultData(
                status,
                value
            )
        ) }
    }

    fun byDataBinding() : ObservableField<ReturnType> = ObservableField<ReturnType>().apply {
        enqueue { _, value ->  value?.let { set(it) } }
    }

    private var synthesizedByteArrayList : ArrayList<Byte> = ArrayList()
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        if(status == 0) {
            characteristic?.value?.let { partialData ->
                if(endSignal == 0x00.toChar().toString()) {
                    try {
                        callback.invoke(0, when(mRequestType) {
                            NOTIFY::class.java, INDICATE::class.java -> mBlueberryRequestInfo.blueberryConverterPrev.convertStringToObject(
                                mBlueberryRequestInfo.mReturnTypeClass as Class<ReturnType>,
                                String(partialData)
                            )
                            else -> null
                        })
                    } catch(exception : Exception) { BlueberryLogger.e("Exception Occured While Parsing Data String", exception)}
                } else {
                    when(String(partialData)) {
                        endSignal -> {
                            try {
                                callback.invoke(0, when(mRequestType) {
                                    NOTIFY::class.java, INDICATE::class.java -> mBlueberryRequestInfo.blueberryConverterPrev.convertStringToObject(
                                        mBlueberryRequestInfo.mReturnTypeClass as Class<ReturnType>,
                                        String(synthesizedByteArrayList.toTypedArray().toByteArray())
                                    )
                                    else -> null
                                })
                                synthesizedByteArrayList.clear()
                            } catch(exception : Exception) { BlueberryLogger.e("Exception Occured While Parsing Data String", exception) }
                        }
                        else -> synthesizedByteArrayList.addAll(partialData.toList())
                    }
                }
            }
        } else super.onResponse(status, characteristic)
    }

}