package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetannotations.INDICATE
import com.gmail.ayteneve93.blueberrysherbetannotations.NOTIFY
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import io.reactivex.Observable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class BlueberryRequestInfoWithRepetitiousResults<ReturnType>(
    uuid : UUID,
    priority : Int,
    awaitingMills : Int,
    blueberryRequest : BlueberryAbstractRequest<ReturnType>,
    requestType : Class<out Annotation>,
    private val startString : String,
    private val endString : String
) : BlueberryAbstractRequestInfo(uuid, priority, awaitingMills, blueberryRequest as BlueberryAbstractRequest<out Any>, requestType) {

    internal var isNotificationEnabled = true
    private lateinit var callback : BlueberryCallbackWithResult<ReturnType>

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) blueberryRequest.mReturnTypeClass.typeName
        else blueberryRequest.mReturnTypeClass.simpleName
        this["Start String"] = startString
        this["End String"] = endString
    }

    override fun cancel() {
        isNotificationEnabled = false
        super.cancel()
    }

    fun enqueue(callback : BlueberryCallbackWithResult<ReturnType>) {
        this.callback = callback
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }

    fun byRx2() : Observable<BlueberryCallbackResultData<ReturnType>>
            = Observable.create { emitter ->
        enqueue { status, value -> emitter.onNext(BlueberryCallbackResultData(status, value)) }
    }

    fun byDataBinding() : ObservableField<ReturnType> = ObservableField<ReturnType>().apply {
        enqueue { _, value ->  value?.let { set(it) } }
    }

    private var synthesizedByteArrayList : ArrayList<Byte> = ArrayList()
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        characteristic?.value?.let { partialData ->
            when(String(partialData)) {
                startString -> return@let
                endString -> {
                    try {
                        callback.invoke(0, when(requestType) {
                            NOTIFY::class.java, INDICATE::class.java -> convertStringToObject<ReturnType>(
                                blueberryRequest.mReturnTypeClass,
                                String(synthesizedByteArrayList.toTypedArray().toByteArray()),
                                blueberryRequest.mMoshi)
                            else -> null
                        })
                        synthesizedByteArrayList.clear()
                    } catch(exception : Exception) { BlueberryLogger.e("Exception Occured While Parsing Data String", exception) }
                }
                else -> synthesizedByteArrayList.addAll(partialData.toList())
            }
        }
    }

}