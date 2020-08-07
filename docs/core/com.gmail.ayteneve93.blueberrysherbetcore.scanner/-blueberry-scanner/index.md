[core](../../index.md) / [com.gmail.ayteneve93.blueberrysherbetcore.scanner](../index.md) / [BlueberryScanner](./index.md)

# BlueberryScanner

(JVM) `object BlueberryScanner`

### Properties

| Name | Summary |
|---|---|
| (JVM) [bleScanPermissionRequestMessage](ble-scan-permission-request-message.md) | `var bleScanPermissionRequestMessage: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| (JVM) [bleScanPermissionRequestTitle](ble-scan-permission-request-title.md) | `var bleScanPermissionRequestTitle: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| (JVM) [isScanning](is-scanning.md) | `val isScanning: ObservableField<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`!>` |

### Functions

| Name | Summary |
|---|---|
| (JVM) [rxStartScan](rx-start-scan.md) | `fun rxStartScan(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`): Observable<`[`BlueberryScanResult`](../-blueberry-scan-result/index.md)`>`<br>`fun rxStartScan(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, filters: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`ScanFilter`](https://developer.android.com/reference/android/bluetooth/le/ScanFilter.html)`>, scanSettings: `[`ScanSettings`](https://developer.android.com/reference/android/bluetooth/le/ScanSettings.html)`): Observable<`[`BlueberryScanResult`](../-blueberry-scan-result/index.md)`!>!` |
| (JVM) [stopScan](stop-scan.md) | `fun stopScan(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
