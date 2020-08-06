BlueberrySherbet
================

BlueberrySherbet is a fast and efficient open source BLE(Bluetooth Low Energy) master device module for Android that interfaces with various Bluetooth devices. For detailed instruction, please see [Blueberry Sherbet API Documents](https://apexcaptain.github.io/BlueberrySherbet/ "GitHub Pages").

![](ReadMeRes/logoWithText.png)

BlueberrySherbet supports [READ], [WRITE], [WRITE_WITHOUT_RESPONSE], [NOTIFY]
 and [INDICATE] methods. If you declare BLE API as Kotlin interface, BlueberrySherbet turns your interface into implemented service class file. Each API from the created service can make an asynchronous BLE request to the connected device. Every call can be converted as a form of RxJava2, Coroutine or just simple callback.

Download
========

#### Step 1. Set the versionf of BlueberrySherbet as external constant in root build.gradle
```gradle
buildscript {
    ext {
        blueberry_sherbet_version = '0.1.8' 
        // ↑ The very name of version constant could be anyhing you want :)
    }
}
```
#### Step 2. Then, add following JitPack url in your root build.gradle at the end of repositories :
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```
#### Step 3. Apply kapt plugin in your app.gradle :
```gradle
apply plugin: 'kotlin-kapt'
```

#### Step 4. Add the following dependency of BlueberrySherbet :
```gradle
dependencies {
    implementation "com.github.ApexCaptain.BlueberrySherbet:annotations:$blueberry_sherbet_version"
    implementation "com.github.ApexCaptain.BlueberrySherbet:core:$blueberry_sherbet_version"
    kapt "com.github.ApexCaptain.BlueberrySherbet:apt:$blueberry_sherbet_version"
}
```

How do I use BlueberrySherbet?
==============================
#### Step 1. Define data classes :
Imagine if you're making an IoT device with ESP32 or Raspberry Pi. In most cases, you would like to figure out its wireless network status.
Then, the data class of your request would something like this
```kotlin
@Keep data class WifiStatus(
    val connectionState : Boolean,
    val ssid : String,
    @Json(name = "ip_address")
    val ipAddress : String
)
```
To make BLE slave device connect to wifi network, you may need to pass exact information. It might look like this

```kotlin
@Keep data class WifiConnectionInfo(
    val ssid : String,
    val psk : String,
    val timeout : Int
)
```
#### Step 2. Declare BLE API :
BLE API is quite similar to normal REST API
```kotlin
@BlueberryService // ← Annotation written to declare BLE API interface
interface TestDeviceService {
    /*
        ↓ This annotation indicates following method uses BLE API,
          of which characteristic uuid is 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601'
          and the BLE method is WRITE
    */
    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601")
    fun connectWifi(wifiConnectionInfo: WifiConnectionInfo) : BlueberryWriteRequest

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601")
    fun checkWifiStatus() : BlueberryReadRequest<WifiStatus>
}
```
##### Note : [WRITE] method can only transfer data to target deivce and [READ] method can only receive'em

#### Step 3. Build the project
When you build the project, the annotation processor of BlueberrySherbet would automatically generate 
implemented BLE API class files as following.
##### Note : You do not have to make it on your own. Cosider it done!
```kotlin
class BlueberryTestDeviceServiceImpl(
  mBlueberryDevice: BlueberryDevice<TestDeviceService>
) : TestDeviceService {
  private val mBlueberryDevice: BlueberryDevice<TestDeviceService>

  private var mMoshi: Moshi
  init {
    this.mBlueberryDevice = mBlueberryDevice
    this.mMoshi = com.squareup.moshi.Moshi.Builder().build()
  }

  fun addMoshiAdapters(vararg adapters: Any) {
    this.mMoshi = this.mMoshi.newBuilder().apply {
        adapters.forEach { add(it) }
    }.build()
  }

  final override fun connectWifi(wifiConnectionInfo: WifiConnectionInfo): BlueberryWriteRequest =
      com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequest(
      mMoshi,
      mBlueberryDevice,
      10,
      "aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601",
      wifiConnectionInfo,
      false
  )
  final override fun checkWifiStatus(): BlueberryReadRequest<WifiStatus> =
      com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequest<com.gmail.ayteneve93.blueberryshertbettestapplication.test.WifiStatus>(
      com.gmail.ayteneve93.blueberryshertbettestapplication.test.WifiStatus::class.java,
      mMoshi,
      mBlueberryDevice,
      10,
      "aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601"
  )}
```
License
=======

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[BlueberryService]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-blueberry-service/index.html
[INDICATE]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-i-n-d-i-c-a-t-e/index.html
[NOTIFY]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-n-o-t-i-f-y/index.html
[Priority]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-priority/index.html
[READ]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-r-e-a-d/index.html
[WRITE]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-w-r-i-t-e/index.html
[WRITE_WITHOUT_RESPONSE]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-w-r-i-t-e_-w-i-t-h-o-u-t_-r-e-s-p-o-n-s-e/index.html