[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [INDICATE](./index.md)

# INDICATE

(JVM) `@Target([AnnotationTarget.FUNCTION]) annotation class INDICATE`

BLE function annotation for 'Indicate' method.

This annotation is used to declare 'Indicate' method BLE function.

Example Code

```
    @BlueberryService
    interface YourBleService {
        @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
        fun funcA()
    }
```

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | BLE function annotation for 'Indicate' method.`INDICATE(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = 0x00.toChar().toString())` |

### Properties

| Name | Summary |
|---|---|
| (JVM) [endSignal](end-signal.md) | Indication end signal string.`val endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| (JVM) [uuidString](uuid-string.md) | UUID value in string format of BLE characteristic.`val uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
