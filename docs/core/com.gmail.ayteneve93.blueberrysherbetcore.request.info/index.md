[core](../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.request.info](./index.md)

## Package com.gmail.ayteneve93.blueberrysherbetcore.request.info

### Types

| Name | Summary |
|---|---|
| (JVM) [BlueberryAbstractRequestInfo](-blueberry-abstract-request-info/index.md) | `abstract class BlueberryAbstractRequestInfo : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<`[`BlueberryAbstractRequestInfo`](-blueberry-abstract-request-info/index.md)`>` |
| (JVM) [BlueberryCallbackResultData](-blueberry-callback-result-data/index.md) | `data class BlueberryCallbackResultData<ReturnType>` |
| (JVM) [BlueberryCallbackWithoutResult](-blueberry-callback-without-result.md) | `typealias BlueberryCallbackWithoutResult = (status: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [BlueberryCallbackWithResult](-blueberry-callback-with-result.md) | `typealias BlueberryCallbackWithResult<ReturnType> = (status: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, value: ReturnType?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [BlueberryRequestInfoWithNoResponse](-blueberry-request-info-with-no-response/index.md) | `class BlueberryRequestInfoWithNoResponse : `[`BlueberryAbstractRequestInfo`](-blueberry-abstract-request-info/index.md) |
| (JVM) [BlueberryRequestInfoWithoutResult](-blueberry-request-info-without-result/index.md) | `class BlueberryRequestInfoWithoutResult : `[`BlueberryAbstractRequestInfo`](-blueberry-abstract-request-info/index.md) |
| (JVM) [BlueberryRequestInfoWithRepetitiousResults](-blueberry-request-info-with-repetitious-results/index.md) | `class BlueberryRequestInfoWithRepetitiousResults<ReturnType> : `[`BlueberryAbstractRequestInfo`](-blueberry-abstract-request-info/index.md) |
| (JVM) [BlueberryRequestInfoWithSimpleResult](-blueberry-request-info-with-simple-result/index.md) | `class BlueberryRequestInfoWithSimpleResult<ReturnType> : `[`BlueberryAbstractRequestInfo`](-blueberry-abstract-request-info/index.md) |
