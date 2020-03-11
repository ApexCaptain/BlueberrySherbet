package com.gmail.ayteneve93.blueberrysherbetannotations




/**
 * Entry point annotation indicating that target interface is apparently a set of BLE methods service.
 *
 * To configure static BlE methods service, it is necessary to be explicitly declared at the very
 *
 * top of the each interface code before you set any further functions.
 *
 * Example Code
 *
 *      @BlueberryService
 *      interface YourBleService {
 *          fun funcA()
 *          fun funcB()
 *          // and so on...
 *      }
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class BlueberryService




/**
 * Optional annotation indicating execution priority of BLE function.
 *
 * You can specify execution priority of each BLE function in form of integer.
 *
 * Basically, the lower its value, it'd have higher priority.
 *
 * And the higher priority it has, the sooner it'd be executed.
 *
 * For instance, when you set 2 different BLE functions; 'funcA', 'funcB',
 *
 * and let's say that 'funcA' is much more important than the other one, so it
 *
 * should be executed before 'funcB', then you can declare them as example code below
 *
 * Example Code
 *
 *      @BlueberryService
 *      interface YourBleService {
 *          @Priority(1) // ← It'll be executed sooner though it's requested lately.
 *          fun funcA()
 *          @Priority(2)
 *          fun funcB()
 *      }
 *
 * If it's not set, the priority of each function would be set as default.
 *
 * @property priority Priority value
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Priority(val priority : Int) {
    companion object {
        /**
         * Static default priority value. Normally, it is set 10.
         *
         * It's possible to regulate this default value if necessary
         *
         * and it does not have to be done during the compile sequence.
         */
        var defaultPriority = 10
    }
}




/**
 * BLE function annotation for 'Read' method.
 *
 * This annotation is used to declare 'Read' method BLE function.
 *
 * Example Code
 *
 *      @BlueberryService
 *      interface YourBleService {
 *          @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
 *          fun funcA()
 *      }
 *
 * @property uuidString UUID value in string format of BLE characteristic.
 *
 * It must be matched with one of the following regular expressions
 *
 *      1. [0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}
 *
 *      2. [0-9a-fA-F]{32}
 *
 *      3. [0-9a-fA-F]{4}
 *
 * Or, it will be crashed during the compiling process
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class READ(val uuidString : String)




/**
 * BLE function annotation for 'Write' method.
 *
 * This annotation is used to declare 'Write' method BLE function.
 *
 * Example Code
 *
 *      @BlueberryService
 *      interface YourBleService {
 *          @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
 *          fun funcA()
 *      }
 *
 * @property checkIsReliable Set true when it should be reliable write.
 * @property uuidString UUID value in string format of BLE characteristic.
 *
 * It must be matched with one of the following regular expressions
 *
 *      1. [0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}
 *
 *      2. [0-9a-fA-F]{32}
 *
 *      3. [0-9a-fA-F]{4}
 *
 * Or, it will be crashed during the compiling process
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WRITE(val uuidString : String, val checkIsReliable : Boolean = false)




/**
 * BLE function annotation for 'Write Without Response' method.
 *
 * This annotation is used to declare 'Write Without Response' method BLE function.
 *
 * Example Code
 *
 *      @BlueberryService
 *      interface YourBleService {
 *          @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
 *          fun funcA()
 *      }
 *
 * @property checkIsReliable Set true when it should be reliable write.
 * @property uuidString UUID value in string format of BLE characteristic.
 *
 * It must be matched with one of the following regular expressions
 *
 *      1. [0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}
 *
 *      2. [0-9a-fA-F]{32}
 *
 *      3. [0-9a-fA-F]{4}
 *
 * Or, it will be crashed during the compiling process
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WRITE_WITHOUT_RESPONSE(val uuidString : String, val checkIsReliable : Boolean = false)




/**
 * BLE function annotation for 'Notify' method.
 *
 * This annotation is used to declare 'Notify' method BLE function.
 *
 * Example Code
 *
 *      @BlueberryService
 *      interface YourBleService {
 *          @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
 *          fun funcA()
 *      }
 *
 * @property uuidString UUID value in string format of BLE characteristic.
 *
 * It must be matched with one of the following regular expressions
 *
 *      1. [0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}
 *
 *      2. [0-9a-fA-F]{32}
 *
 *      3. [0-9a-fA-F]{4}
 *
 * Or, it will be crashed during the compiling process
 * @property endSignal Notification end signal string.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class NOTIFY(val uuidString : String, val endSignal : String)




/**
 * BLE function annotation for 'Indicate' method.
 *
 * This annotation is used to declare 'Indicate' method BLE function.
 *
 * Example Code
 *
 *      @BlueberryService
 *      interface YourBleService {
 *          @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
 *          fun funcA()
 *      }
 *
 * @property uuidString UUID value in string format of BLE characteristic.
 *
 * It must be matched with one of the following regular expressions
 *
 *      1. [0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}
 *
 *      2. [0-9a-fA-F]{32}
 *
 *      3. [0-9a-fA-F]{4}
 *
 * Or, it will be crashed during the compiling process
 * @property endSignal Indication end signal string.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class INDICATE(val uuidString : String,  val endSignal : String)

