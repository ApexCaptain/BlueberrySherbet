[core](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.request.info](../index.md) / [BlueberryAbstractRequestInfo](./index.md)

# BlueberryAbstractRequestInfo

(JVM) `abstract class BlueberryAbstractRequestInfo : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<`[`BlueberryAbstractRequestInfo`](./index.md)`>`

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | `BlueberryAbstractRequestInfo(mUuid: `[`UUID`](https://docs.oracle.com/javase/6/docs/api/java/util/UUID.html)`, mPriority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, mAwaitingMills: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, mBlueberryRequest: `[`BlueberryAbstractRequest`](../../com.gmail.ayteneve93.blueberrysherbetcore.request/-blueberry-abstract-request/index.md)`<out `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>, mRequestType: `[`Class`](https://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<out `[`Annotation`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)`>)` |

### Functions

| Name | Summary |
|---|---|
| (JVM) [cancel](cancel.md) | `open fun cancel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [compareTo](compare-to.md) | `open fun compareTo(other: `[`BlueberryAbstractRequestInfo`](./index.md)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| (JVM) [convertToSimpleHashMap](convert-to-simple-hash-map.md) | `open fun convertToSimpleHashMap(): `[`HashMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-hash-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?>` |
| (JVM) [toString](to-string.md) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| (JVM) [BlueberryRequestInfoWithNoResponse](../-blueberry-request-info-with-no-response/index.md) | `class BlueberryRequestInfoWithNoResponse : `[`BlueberryAbstractRequestInfo`](./index.md) |
| (JVM) [BlueberryRequestInfoWithoutResult](../-blueberry-request-info-without-result/index.md) | `class BlueberryRequestInfoWithoutResult : `[`BlueberryAbstractRequestInfo`](./index.md) |
| (JVM) [BlueberryRequestInfoWithRepetitiousResults](../-blueberry-request-info-with-repetitious-results/index.md) | `class BlueberryRequestInfoWithRepetitiousResults<ReturnType> : `[`BlueberryAbstractRequestInfo`](./index.md) |
| (JVM) [BlueberryRequestInfoWithSimpleResult](../-blueberry-request-info-with-simple-result/index.md) | `class BlueberryRequestInfoWithSimpleResult<ReturnType> : `[`BlueberryAbstractRequestInfo`](./index.md) |
