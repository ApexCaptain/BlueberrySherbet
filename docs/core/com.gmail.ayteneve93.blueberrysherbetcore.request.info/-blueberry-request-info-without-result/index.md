[core](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.request.info](../index.md) / [BlueberryRequestInfoWithoutResult](./index.md)

# BlueberryRequestInfoWithoutResult

(JVM) `class BlueberryRequestInfoWithoutResult : `[`BlueberryAbstractRequestInfo`](../-blueberry-abstract-request-info/index.md)

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | `BlueberryRequestInfoWithoutResult(uuid: `[`UUID`](https://docs.oracle.com/javase/6/docs/api/java/util/UUID.html)`, priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, awaitingMills: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, blueberryRequest: `[`BlueberryAbstractRequest`](../../com.gmail.ayteneve93.blueberrysherbetcore.request/-blueberry-abstract-request/index.md)`<out `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>, requestType: `[`Class`](https://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<out `[`Annotation`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)`>, inputString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, checkIsReliable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| (JVM) [checkIsReliable](check-is-reliable.md) | `val checkIsReliable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| Name | Summary |
|---|---|
| (JVM) [byCoroutine](by-coroutine.md) | `suspend fun byCoroutine(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| (JVM) [byRx2](by-rx2.md) | `fun byRx2(): Single<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |
| (JVM) [convertToSimpleHashMap](convert-to-simple-hash-map.md) | `fun convertToSimpleHashMap(): `[`HashMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-hash-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?>` |
| (JVM) [enqueue](enqueue.md) | `fun enqueue(callback: `[`BlueberryCallbackWithoutResult`](../-blueberry-callback-without-result.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
