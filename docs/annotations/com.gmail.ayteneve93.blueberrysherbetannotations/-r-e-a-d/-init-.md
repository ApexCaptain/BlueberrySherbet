[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [READ](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

(JVM) `READ(uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

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

