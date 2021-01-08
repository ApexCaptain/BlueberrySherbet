package com.gmail.ayteneve93.blueberrysherbetcore.request.call

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Handler
import android.os.Looper
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequestInfo
import java.util.*
import kotlin.collections.HashMap

abstract class BlueberryAbstractRequest (
    internal val mUuid : UUID,
    internal var mPriority : Int,
    internal var mAwaitingMills : Int,
    internal val mBlueberryRequestInfo : BlueberryAbstractRequestInfo<out Any>,
    internal val mRequestType : Class<out Annotation>
) : Comparable<BlueberryAbstractRequest> {
    internal val mRequestCode : Long = requestCount++
    internal var mIsOnProgress : Boolean = false
    internal val mRequestTimer = Handler(Looper.getMainLooper())

    protected open fun convertToSimpleHashMap() : HashMap<String, Any?> = HashMap<String, Any?>().apply {
        this["UUID"] = mUuid
        this["Priority"] = mPriority
        this["Awaiting Milliseconds"] = mAwaitingMills
        this["Request Type"] = mRequestType.simpleName
        this["Request Code"] = mRequestCode
        this["Is On Progress"] = mIsOnProgress
    }

    override fun compareTo(other: BlueberryAbstractRequest): Int =
        if(this.mPriority == other.mPriority) this.mRequestCode.compareTo(other.mRequestCode)
        else this.mPriority.compareTo(other.mPriority)

    override fun toString(): String = this::class.java.simpleName + convertToSimpleHashMap().toList().joinToString(
        separator = "\n # ",
        prefix = "\n # ",
        transform = {
            "${it.first} : ${it.second}"
        }
    )

    internal fun startTimer() {
        mRequestTimer.postDelayed({
            mBlueberryRequestInfo.mBlueberryDevice.disconnect()
        }, mAwaitingMills.toLong())
    }

    open fun cancel() = mBlueberryRequestInfo.mBlueberryDevice.cancelBlueberryRequest(this)
    internal open fun onResponse(status : Int?, characteristic : BluetoothGattCharacteristic?) {
        mIsOnProgress = false
        mRequestTimer.removeCallbacksAndMessages(null)
    }

    companion object {
        private var requestCount : Long = 0L
    }


}