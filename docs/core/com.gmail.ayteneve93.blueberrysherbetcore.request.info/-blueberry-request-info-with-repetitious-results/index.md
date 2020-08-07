[core](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.request.info](../index.md) / [BlueberryRequestInfoWithRepetitiousResults](./index.md)

# BlueberryRequestInfoWithRepetitiousResults

(JVM) `class BlueberryRequestInfoWithRepetitiousResults<ReturnType> : `[`BlueberryAbstractRequestInfo`](../-blueberry-abstract-request-info/index.md)

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | `BlueberryRequestInfoWithRepetitiousResults(uuid: `[`UUID`](https://docs.oracle.com/javase/6/docs/api/java/util/UUID.html)`, priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, awaitingMills: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, blueberryRequest: `[`BlueberryAbstractRequest`](../../com.gmail.ayteneve93.blueberrysherbetcore.request/-blueberry-abstract-request/index.md)`<ReturnType>, requestType: `[`Class`](https://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<out `[`Annotation`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)`>, endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Functions

| Name | Summary |
|---|---|
| (JVM) [byDataBinding](by-data-binding.md) | `fun byDataBinding(): ObservableField<ReturnType>` |
| (JVM) [byRx2](by-rx2.md) | `fun byRx2(): Observable<`[`BlueberryCallbackResultData`](../-blueberry-callback-result-data/index.md)`<ReturnType>>` |
| (JVM) [cancel](cancel.md) | `fun cancel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [convertToSimpleHashMap](convert-to-simple-hash-map.md) | `fun convertToSimpleHashMap(): `[`HashMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-hash-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?>` |
| (JVM) [enqueue](enqueue.md) | `fun enqueue(callback: `[`BlueberryCallbackWithResult`](../-blueberry-callback-with-result.md)`<ReturnType>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
