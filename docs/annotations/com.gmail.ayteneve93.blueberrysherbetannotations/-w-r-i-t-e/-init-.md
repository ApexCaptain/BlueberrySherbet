[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [WRITE](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

(JVM) `WRITE(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, checkIsReliable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`

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

