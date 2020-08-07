[core](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.request](../index.md) / [BlueberryAbstractRequest](./index.md)

# BlueberryAbstractRequest

(JVM) `abstract class BlueberryAbstractRequest<ReturnType>`

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | `BlueberryAbstractRequest(mReturnTypeClass: `[`Class`](https://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<ReturnType>, mMoshi: Moshi, mBlueberryDevice: `[`BlueberryDevice`](../../com.gmail.ayteneve93.blueberrysherbetcore.device/-blueberry-device/index.md)`<out `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>, mPriority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, uuidString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Functions

| Name | Summary |
|---|---|
| (JVM) [addMoshiAdapters](add-moshi-adapters.md) | `fun addMoshiAdapters(vararg adapters: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [call](call.md) | `abstract fun call(awaitingMills: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 29000): `[`BlueberryAbstractRequestInfo`](../../com.gmail.ayteneve93.blueberrysherbetcore.request.info/-blueberry-abstract-request-info/index.md) |
| (JVM) [convertToSimpleHashMap](convert-to-simple-hash-map.md) | `open fun convertToSimpleHashMap(): `[`HashMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-hash-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?>` |
| (JVM) [toString](to-string.md) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| (JVM) [BlueberryNotifyOrIndicateRequest](../-blueberry-notify-or-indicate-request/index.md) | `class BlueberryNotifyOrIndicateRequest<ReturnType> : `[`BlueberryAbstractRequest`](./index.md)`<ReturnType>` |
| (JVM) [BlueberryReadRequest](../-blueberry-read-request/index.md) | `class BlueberryReadRequest<ReturnType> : `[`BlueberryAbstractRequest`](./index.md)`<ReturnType>` |
| (JVM) [BlueberryWriteRequest](../-blueberry-write-request/index.md) | `class BlueberryWriteRequest : `[`BlueberryAbstractRequest`](./index.md)`<`[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` |
| (JVM) [BlueberryWriteRequestWithoutResponse](../-blueberry-write-request-without-response/index.md) | `class BlueberryWriteRequestWithoutResponse : `[`BlueberryAbstractRequest`](./index.md)`<`[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` |
