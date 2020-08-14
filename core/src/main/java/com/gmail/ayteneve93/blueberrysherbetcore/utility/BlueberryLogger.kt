package com.gmail.ayteneve93.blueberrysherbetcore.utility

import android.util.Log
import androidx.annotation.Keep
import com.gmail.ayteneve93.blueberrysherbetcore.BuildConfig

@Keep
object BlueberryLogger {

    var isReleaseBuildOutLoggingEnabled = false
    var isStackTraceOutLoggingEnabled = true
    var stackTraceDepth = 5
    var isThreadNameOutLoggingEnabled = true

    private val TAG = this::class.java.simpleName



    private fun getStackTrace() : String =
        Exception().stackTrace.let { stackTrace ->
            var depth = stackTraceDepth + 3
            if(depth > stackTrace.size - 1) depth = stackTrace.size - 1
            stackTrace.slice(4..depth).joinToString(
                prefix = "\n→  ",
                separator = "\n→  "
            )
        }

    private fun generateLogString(message : String) : String =
        "${if(isThreadNameOutLoggingEnabled) "#<${Thread.currentThread().name}> -- " else ""}$message ${if(isStackTraceOutLoggingEnabled) "\n-- Called From --${getStackTrace()}\n" else ""}"

    internal fun v(message : String, throwable : Throwable? = null) { if(isReleaseBuildOutLoggingEnabled || BuildConfig.DEBUG) Log.v(TAG, generateLogString(message), throwable) }
    internal fun d(message : String, throwable : Throwable? = null) { if(isReleaseBuildOutLoggingEnabled || BuildConfig.DEBUG) Log.d(TAG, generateLogString(message), throwable) }
    internal fun i(message : String, throwable : Throwable? = null) { if(isReleaseBuildOutLoggingEnabled || BuildConfig.DEBUG) Log.i(TAG, generateLogString(message), throwable) }
    internal fun w(message : String, throwable : Throwable? = null) { if(isReleaseBuildOutLoggingEnabled || BuildConfig.DEBUG) Log.w(TAG, generateLogString(message), throwable) }
    internal fun e(message : String, throwable : Throwable? = null) { if(isReleaseBuildOutLoggingEnabled || BuildConfig.DEBUG) Log.e(TAG, generateLogString(message), throwable) }

}



