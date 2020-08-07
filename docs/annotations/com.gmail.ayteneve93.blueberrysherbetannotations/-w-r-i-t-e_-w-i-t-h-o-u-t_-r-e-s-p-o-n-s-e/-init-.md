[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [WRITE_WITHOUT_RESPONSE](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

(JVM) `WRITE_WITHOUT_RESPONSE(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, checkIsReliable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`

BLE function annotation for 'Write Without Response' method.

This annotation is used to declare 'Write Without Response' method BLE function.

Example Code

```
    @BlueberryService
    interface YourBleService {
        @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
        fun funcA()
    }
```

