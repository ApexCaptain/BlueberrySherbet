package com.gmail.ayteneve93.blueberrysherbetcore.request.info

typealias BlueberryCallbackWithResult<ReturnType> = (status : Int, value : ReturnType?) -> Unit

typealias BlueberryCallbackWithoutResult = (status : Int) -> Unit

data class BlueberryCallbackResultData<ReturnType>(val status : Int, val value : ReturnType?)