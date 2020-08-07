[core](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.device](../index.md) / [BlueberryDevice](./index.md)

# BlueberryDevice

(JVM) `abstract class BlueberryDevice<BlueberryService>`

Abstract class of BLE device.

You can configure specific options for each BLE device by inheriting this abstract class.

### Types

| Name | Summary |
|---|---|
| (JVM) [BleStatus](-ble-status/index.md) | `enum class BleStatus` |
| (JVM) [BlueberryConnectionPriority](-blueberry-connection-priority/index.md) | B`enum class BlueberryConnectionPriority` |
| (JVM) [BluetoothState](-bluetooth-state/index.md) | `enum class BluetoothState` |
| (JVM) [PhyOption](-phy-option/index.md) | `enum class PhyOption` |

### Constructors

| Name | Summary |
|---|---|
| (JVM) [&lt;init&gt;](-init-.md) | Abstract class of BLE device.`BlueberryDevice()` |

### Properties

| Name | Summary |
|---|---|
| (JVM) [autoConnect](auto-connect.md) | `var autoConnect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (JVM) [blueberryService](blueberry-service.md) | `val blueberryService: BlueberryService` |
| (JVM) [bluetoothState](bluetooth-state.md) | `val bluetoothState: ObservableField<BluetoothState!>` |
| (JVM) [mBluetoothGatt](m-bluetooth-gatt.md) | `lateinit var mBluetoothGatt: `[`BluetoothGatt`](https://developer.android.com/reference/android/bluetooth/BluetoothGatt.html) |
| (JVM) [mtuBinding](mtu-binding.md) | `val mtuBinding: ObservableField<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |
| (JVM) [rssiBinding](rssi-binding.md) | `val rssiBinding: ObservableField<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |
| (JVM) [rxPhyBinding](rx-phy-binding.md) | `val rxPhyBinding: ObservableField<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |
| (JVM) [txPhyBinding](tx-phy-binding.md) | `val txPhyBinding: ObservableField<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |

### Functions

| Name | Summary |
|---|---|
| (JVM) [connect](connect.md) | Basic Connection Controlling Operation`fun connect(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [disconnect](disconnect.md) | `fun disconnect(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [dismissRssiUpdateInterval](dismiss-rssi-update-interval.md) | `fun dismissRssiUpdateInterval(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [equals](equals.md) | Override Functions`open fun equals(other: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (JVM) [hashCode](hash-code.md) | `open fun hashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| (JVM) [onDeviceConnected](on-device-connected.md) | `open fun onDeviceConnected(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [onDeviceConnecting](on-device-connecting.md) | `open fun onDeviceConnecting(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [onDeviceDisconnected](on-device-disconnected.md) | Device Life Cycle Callback`open fun onDeviceDisconnected(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [onDeviceDisconnecting](on-device-disconnecting.md) | `open fun onDeviceDisconnecting(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [onMtuValueChanged](on-mtu-value-changed.md) | `open fun onMtuValueChanged(mtu: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [onPhyValueChanged](on-phy-value-changed.md) | `open fun onPhyValueChanged(txPhy: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, rxPhy: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [onRssiValueChanged](on-rssi-value-changed.md) | `open fun onRssiValueChanged(rssiValue: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [onServicesDiscovered](on-services-discovered.md) | `open fun onServicesDiscovered(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [readPhy](read-phy.md) | `fun readPhy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [requestMtu](request-mtu.md) | `fun requestMtu(mtu: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [setConnectionPriority](set-connection-priority.md) | Change Connection Priority`fun setConnectionPriority(connectionPriority: BlueberryConnectionPriority): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [setPreferredPhy](set-preferred-phy.md) | `fun setPreferredPhy(txPhy: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, rxPhy: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [setRssiUpdateInterval](set-rssi-update-interval.md) | `fun setRssiUpdateInterval(intervalTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, intervalTimeUnit: `[`TimeUnit`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/TimeUnit.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| (JVM) [setServiceImpl](set-service-impl.md) | `abstract fun setServiceImpl(): BlueberryService` |
| (JVM) [toString](to-string.md) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
