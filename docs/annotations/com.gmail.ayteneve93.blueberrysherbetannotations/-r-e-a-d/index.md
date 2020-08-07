[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [READ](./index.md)

# READ

(JVM) `@Target([AnnotationTarget.FUNCTION]) annotation class READ`

BLE function annotation for 'Read' method.

This annotation is used to declare 'Read' method BLE function.

Example Code

```
    @BlueberryService
    interface YourBleService {
        @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
        fun funcA()
    }
```

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | BLE function annotation for 'Read' method.`READ(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| (JVM) [uuidString](uuid-string.md) | UUID value in string format of BLE characteristic.`val uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
