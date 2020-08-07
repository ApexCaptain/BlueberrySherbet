[annotations](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetannotations](../index.md) / [Priority](./index.md)

# Priority

(JVM) `@Target([AnnotationTarget.FUNCTION]) annotation class Priority`

Optional annotation indicating execution priority of BLE function.

You can specify execution priority of each BLE function in form of integer.

Basically, the lower its value, it'd have higher priority.

And the higher priority it has, the sooner it'd be executed.

For instance, when you set 2 different BLE functions; 'funcA', 'funcB',

and let's say that 'funcA' is much more important than the other one, so it

should be executed before 'funcB', then you can declare them as example code below

Example Code

```
    @BlueberryService
    interface YourBleService {
        @Priority(1) // ← It'll be executed sooner though it's requested lately.
        fun funcA()
        @Priority(2)
        fun funcB()
    }
```

If it's not set, the priority of each function would be set as default.

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | Optional annotation indicating execution priority of BLE function.`Priority(priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| (JVM) [priority](priority.md) | Priority value`val priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| (JVM) [defaultPriority](default-priority.md) | Static default priority value. Normally, it is set 10.`var defaultPriority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
