[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [WRITE](./index.md)

# WRITE

(JVM) `@Target([AnnotationTarget.FUNCTION]) annotation class WRITE`

BLE function annotation for 'Write' method.

This annotation is used to declare 'Write' method BLE function.

Example Code

```
    @BlueberryService
    interface YourBleService {
        @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
        fun funcA()
    }
```

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | BLE function annotation for 'Write' method.`WRITE(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, checkIsReliable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)` |

### Properties

| Name | Summary |
|---|---|
| (JVM) [checkIsReliable](check-is-reliable.md) | Set true when it should be reliable write.`val checkIsReliable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (JVM) [uuidString](uuid-string.md) | UUID value in string format of BLE characteristic.`val uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
