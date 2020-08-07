[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [INDICATE](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

(JVM) `INDICATE(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = 0x00.toChar().toString())`

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

