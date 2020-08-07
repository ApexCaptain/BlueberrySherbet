[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [NOTIFY](./index.md)

# NOTIFY

(JVM) `@Target([AnnotationTarget.FUNCTION]) annotation class NOTIFY`

BLE function annotation for 'Notify' method.

This annotation is used to declare 'Notify' method BLE function.

Example Code

```
    @BlueberryService
    interface YourBleService {
        @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
        fun funcA()
    }
```

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | BLE function annotation for 'Notify' method.`NOTIFY(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = 0x00.toChar().toString())` |

### Properties

| Name | Summary |
|---|---|
| (JVM) [endSignal](end-signal.md) | Notification end signal string.`val endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| (JVM) [uuidString](uuid-string.md) | UUID value in string format of BLE characteristic.`val uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
