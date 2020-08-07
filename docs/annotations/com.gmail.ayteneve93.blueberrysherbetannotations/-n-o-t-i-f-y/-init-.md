[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [NOTIFY](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

(JVM) `NOTIFY(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = 0x00.toChar().toString())`

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

