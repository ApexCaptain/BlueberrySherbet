[core](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.request](../index.md) / [BlueberryNotifyOrIndicateRequest](./index.md)

# BlueberryNotifyOrIndicateRequest

(JVM) `class BlueberryNotifyOrIndicateRequest<ReturnType> : `[`BlueberryAbstractRequest`](../-blueberry-abstract-request/index.md)`<ReturnType>`

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | `BlueberryNotifyOrIndicateRequest(returnTypeClass: `[`Class`](https://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<ReturnType>, moshi: Moshi, blueberryDevice: `[`BlueberryDevice`](../../com.gmail.ayteneve93.blueberrysherbetcore.device/-blueberry-device/index.md)`<out `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>, priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, requestType: `[`Class`](https://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<out `[`Annotation`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)`>, endSignal: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Functions

| Name | Summary |
|---|---|
| (JVM) [call](call.md) | `fun call(awaitingMills: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`BlueberryRequestInfoWithRepetitiousResults`](../../com.gmail.ayteneve93.blueberrysherbetcore.request.info/-blueberry-request-info-with-repetitious-results/index.md)`<ReturnType>` |
| (JVM) [convertToSimpleHashMap](convert-to-simple-hash-map.md) | `fun convertToSimpleHashMap(): `[`HashMap`](https://docs.oracle.com/javase/6/docs/api/java/util/HashMap.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?>` |
