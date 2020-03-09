package com.gmail.ayteneve93.blueberrysherbetannotations

import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class BlueberryService

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Priority(val priority : Int) {
    companion object {
        var defaultPriority = 10
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class READ(val uuidString : String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WRITE(val uuidString : String, val checkIsReliable : Boolean = false)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WRITE_WITHOUT_RESPONSE(val uuidString : String, val checkIsReliable : Boolean = false)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class NOTIFY(val uuidString : String, val endSignal : String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class INDICATE(val uuidString : String,  val endSignal : String)

