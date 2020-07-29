BlueberrySherbet
================

BlueberrySherbet is a fast and efficient open source BLE(Bluetooth Low Energy) master device module for Android that interfaces with various Bluetooth devices. For detailed instruction, please see [Blueberry Sherbet API Documents](https://apexcaptain.github.io/BlueberrySherbet/, "GitHub Pages").

![](ReadMeRes/logoWithText.png)

BlueberrySherbet supports READ, WRITE, WRITE_WITHOUT_RESPONSE,
NOTIFY and INDICATE methods. If you declare BLE API as Kotlin interface, BlueberrySherbet turns your interface into implemented service class file. Each API from the created service can make a asynchronous BLE request to the connected device. Every call can be converted as a form of RxJava2, Coroutine or just simple callback.

Download
========

### Gradle

#### Step 1. Add following in your root build.gradle at the end of repositories :
```gradle
repositories {
  maven { url 'https://jitpack.io' }
}
```
#### Step 2. Add the dependency
```gradle
dependencies {
  implementation "com.github.ApexCaptain.BlueberrySherbet:annotations:$blueberry_sherbet_version"
}
```

### Maven
#### Step 1. Add the JitPack repository to your build file
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
#### Step 2. Add the Dependency
```xml
	<dependency>
	    <groupId>com.github.ApexCaptain.BlueberrySherbet</groupId>
	    <artifactId>annotations</artifactId>
	    <version>0.1.1</version>
	</dependency>
```

How do I use BlueberrySherbet?
==============================
Imagine if you're making an IoT device with ESP32 or Raspberry Pi. In most cases, you would like to figure out its wireless network status.
Then, the data class of your request would something like this:
```kotlin
@Keep data class WifiStatus(
    val connectionState : Boolean,
    val ssid : String,
    @Json(name = "ip_address")
    val ipAddress : String
)
```
To make BLE slave device connect to wifi network, you may need to pass exact information. It might look like this:

```kotlin
@Keep data class WifiConnectionInfo(
    val ssid : String,
    val psk : String,
    val timeout : Int
)
```
Now, you have to set BLE API interface file :
```kotlin
@BlueberryService
interface TestDeviceService {
    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601")
    fun connectWifi(wifiConnectionInfo: WifiConnectionInfo) : BlueberryWriteRequest

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601")
    fun readCheckWifiStatus() : BlueberryReadRequest<WifiStatus>
}
```
Following is the definition of actual BLE device class 

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