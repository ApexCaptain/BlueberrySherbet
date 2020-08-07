[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [BlueberryService](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

(JVM) `BlueberryService()`

Entry point annotation indicating that target interface is apparently a set of BLE methods service.

To configure static BlE methods service, it is necessary to be explicitly declared at the very

top of the each interface code before you set any further functions.

Example Code

```
    @BlueberryService
    interface YourBleService {
        fun funcA()
        fun funcB()
        // and so on...
    }
```

