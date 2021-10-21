[![JitPack](https://img.shields.io/jitpack/v/github/ApexCaptain/BlueberrySherbet?color=GREEN&label=Blueberry%20Sherbet&style=flat-square&logo=Android%20Studio)](https://jitpack.io/#ApexCaptain/BlueberrySherbet)

# <img src="ReadMeRes/logo.png" alt="Blueberry Sherbet Logo" width="70" height="70">BlueberrySherbet : BLE Master Device Framework for Android Native

# Table of Contents

- [Introduction](#Introduction)
- [Installation](#Installation)
- [Example](#Example)
- [Annotation](#Annotation)
- [Converter](#Converter)
- [License](#License)

# Introduction

BlueberrySherbet is a fast and efficient open source [BLE(Bluetooth Low Energy)](https://wikipedia.org/wiki/Bluetooth_Low_Energy) master device framework for Android Application, which interfaces with various slave devices such as [raspberry pi](https://www.raspberrypi.org/) or [ESP32](https://www.espressif.com/en/products/socs/esp32) operating your custom embedded bluetooth software. For detailed instruction, please see [Blueberry Sherbet API Documents](https://apexcaptain.github.io/BlueberrySherbet/ "GitHub Pages").

This framework supports [READ], [WRITE], [WRITE_WITHOUT_RESPONSE], [NOTIFY]
and [INDICATE] methods. If you declare a BLE API as Kotlin interface, BlueberrySherbet turns it into an implemented service class file. Each API from the created service can make an asynchronous BLE request to connected device. Every call can be converted as a form of RxJava2, Coroutine or just simple callback.

- Target SDK : v30
- Minimum SDK : v24

# Installation

### Step 1

Set the version of BlueberrySherbet as an external constant in root build.gradle. You can check latest version [here](https://jitpack.io/#ApexCaptain/BlueberrySherbet).

```gradle
buildscript {
    ext {
        blueberry_sherbet_version = '0.5.2-alpha'
        // ↑ The very name of version constant could be anything you want :)
    }
}
```

### Step 2

Then, add following JitPack url in your root build.gradle at the end of repositories :

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

### Step 3

Apply kapt plugin in your app.gradle :

```gradle
plugins {
        ⋮
    id 'kotlin-kapt'
        ⋮
}
```

### Step 4

Add the following dependency of BlueberrySherbet :

```gradle
dependencies {
    ⋮
    implementation "com.github.ApexCaptain.BlueberrySherbet:core:$blueberry_sherbet_version"
    // ↑ This is for basic usage of API annotations, scanner, etc.
    kapt "com.github.ApexCaptain.BlueberrySherbet:apt:$blueberry_sherbet_version"
    // ↑ Kotlin Annotation Processor
    ⋮
}
```

# Example

Let's make a simple project. I used Raspberry Pi as a slave device and coded example source with typescript. To see entire of it, please have a check [here](https://github.com/ApexCaptain/BlueberrySherbet_RPI_Example).

And for master code, you can check [here](https://github.com/ApexCaptain/BlueberrySherbet/tree/master/app/src/main/java/com/gmail/ayteneve93/blueberryshertbettestapplication/slave).

## Simple String Read

[Here](https://github.com/ApexCaptain/BlueberrySherbet_RPI_Example/blob/main/lib/src/Services/PrimitiveService/Characteristics/StringCharacteristic.ts) is a single characteristic code for slave device.

```typescript
import bleno from "bleno";
import {
  GattUUID,
  ReadRequestCallback,
  WriteRequestCallback,
  UpdateValueCallback,
  ResultCode,
  notifyData,
} from "../../../../Module.internal";
import { EventEmitter } from "events";

export class StringCharacteristic extends bleno.Characteristic {
  private static EVENT_NOTIFY = `EVENT_NOTIFY_${StringCharacteristic.name}`;
  private static sInstance: StringCharacteristic;
  private static sEmitter = new EventEmitter();

  static get instance(): StringCharacteristic {
    if (!this.sInstance) this.sInstance = new StringCharacteristic();
    return this.sInstance;
  }
  private constructor() {
    super({
      uuid: GattUUID.primitiveService.characteristics.stringCharacteristicUuid,
      properties: ["read", "write", "notify"],
    });
    setInterval(() => {
      StringCharacteristic.sEmitter.emit(
        StringCharacteristic.EVENT_NOTIFY,
        "String notification data."
      );
    }, 5000);
  }

  onReadRequest(offset: number, callback: ReadRequestCallback) {
    try {
      const dataToSend = "Hello, Sherbet!";
      const dataBuffer = Buffer.from(dataToSend);
      if (offset > dataBuffer.length) callback(ResultCode.INVALID_OFFSET);
      else callback(ResultCode.SUCCESS, dataBuffer.slice(offset));
    } catch (error) {
      callback(ResultCode.FAILURE);
    }
  }

  onWriteRequest(
    data: Buffer,
    _: number,
    withoutResponse: boolean,
    callback: WriteRequestCallback
  ) {
    try {
      const receivedData = data.toString();
      console.info(`Received Data : ${receivedData}`);
      callback(ResultCode.SUCCESS);
    } catch (error) {
      callback(ResultCode.FAILURE);
    }
  }

  onSubscribe(maxValueSize: number, updateValueCallback: UpdateValueCallback) {
    StringCharacteristic.sEmitter.on(
      StringCharacteristic.EVENT_NOTIFY,
      (data: string) => {
        notifyData(data, maxValueSize, updateValueCallback, "$EoD");
      }
    );
  }

  onUnsubscribe() {
    StringCharacteristic.sEmitter.removeAllListeners(
      StringCharacteristic.EVENT_NOTIFY
    );
  }
}
```

I'll not explain how the source of slave works. There are already various different frameworks and modules supporting BLE. If you're gonna use [nodejs](https://nodejs.org/ko/) as main runtime of slave and want to know how does it work, see [bleno](https://www.npmjs.com/package/bleno).

Keep this in mind, you do not have to understand each and every signle line of the above typescript source. I'll cut only important part for you instead.

```typescript
    ...
  onReadRequest(offset: number, callback: ReadRequestCallback) {
    try {
      const dataToSend = "Hello, Sherbet!";
      const dataBuffer = Buffer.from(dataToSend);
      if (offset > dataBuffer.length) callback(ResultCode.INVALID_OFFSET);
      else callback(ResultCode.SUCCESS, dataBuffer.slice(offset));
    } catch (error) {
      callback(ResultCode.FAILURE);
    }
  }
    ...
```

What matters is the fact that when your android application connects to this specific slave device and requests [READ] method through its [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier), it'll return you a simple string `Hello, Sherbet!`.
<br>

##### TMI : `UUID (Universally Unique Identifier) is like a url string of REST API.`

##### It's not written in literal value up there though, I'll tell you the secret. It's `aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee101`.

<br>
Now, let's get to the android studio.

### Step 1. Declare Interface

First, you have to create an interface service file. You can see source [here](https://github.com/ApexCaptain/BlueberrySherbet/blob/master/app/src/main/java/com/gmail/ayteneve93/blueberryshertbettestapplication/slave/ExampleService.kt).

```kotlin
import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequestInfo

// Type annotation '@BlueberryService' indicates following interface is a BLE service declartion.
@BlueberryService
interface ExampleService {
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee101")
    // ↑ Pass uuid string as an argument.
    fun readSomeString() : BlueberryReadRequestInfo<String> 
    // ↑ Return type of method using '@READ' is always BlueberryReadRequsetInfo<{Result Type}>
}
```

### Step 2. Build it.

Build or Rebuild your project. Annotation processor of BlueberrySherbet will automatically generate implemented service file and its name would be `Blueberry{Your Service Interface Name}Impl`. In this case, it's `BlueberryExampleServiceImpl`.

### Step 3. Create Device Class

Make a device class, of which individual instance is matched 1 to 1 with the actual slave device. You can see full code [here](https://github.com/ApexCaptain/BlueberrySherbet/blob/master/app/src/main/java/com/gmail/ayteneve93/blueberryshertbettestapplication/slave/ExampleDevice.kt).

```kotlin
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
class ExampleDevice : BlueberryDevice<ExampleService> {

    override fun setServiceImpl() : ExampleService = BlueberryExampleServiceImpl(this)

}
```

Example service extends BlueberryDevice, and it has one generic argument, service type. Service type is the interface that we've just created and run building at step 1 and 2. In this case, `ExampleService` would be right. After that, override method `setServiceImpl` returning an instance of actually implemented service class, `BlueberryExampleServiceImpl`.

### Step 4. Scan and connect

I'll take an example of scanning and connecting in acitivty. (or it could be fragment)

```kotlin
import ...
class MainActivity : AppCompatActivity() {
    private lateinit var mExampleDevice : ExampleDevice
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCompositeDisposable.add(
            // Start Scan
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->
                    scanResult.bluetoothDevice.name?.let { advertisingName ->
                        // This is an example. You can add filter as an argument of rxStratScan
                        // or just find it by MAC address.
                        if(advertisingName == "MyDeviceName") {
                            // Stop Scan
                            BlueberryScanner.stopScan()
                            // Instantiate Device Class
                            mExampleDevice = scanResult.interlocl(this. ExampleDevice::class.java)
                            // Connect to the device
                            mExampleDevice.connect()
                        }
                    }
                 }
        )

    }
}
```

Now, your device class is instantiated and connected.

### Step 5. Send Request

After create an instance of your device and connect to it, you can send request in 3 different ways.

Simple callback, reactivex and coroutine.

```kotlin
    // By Simple Callback
    mExampleDevice
        .blueberryService
        .readSomeString()
        .call()
        .enqueue { status, value ->
            Log.d("Test", "status : $status, value : $value")
            // ↑ status 0, value : Hello, Sherbet!
        }

    // By RxJava
    mCompositeDisposable.add(
        mExampleDevice
            .blueberryService
            .readSomeString()
            .call()
            .byRx2()
            .subscribe { result, error ->
                Log.d("Test", "status : ${result.status}, value : ${result.value}")
                // ↑ status 0, value : Hello, Sherbet!

            }
    )

    // By Coroutine
    GlobalScope.launch {
        const result = mExampleDevice
            .blueberryService
            .readSomeString()
            .call()
            .byCoroutine()
        Log.d("Test", "status : ${result.status}, value : ${result.value}")
        // ↑ status 0, value : Hello, Sherbet!
    }
```

That's it! These are the basic usage of BlueberrySherbet.

1. Declare Service Interface
2. Build it
3. Create Device Class
4. Scan and connect where you want to use BLE device.
5. Send request and get result.

# Annotation
There are some annotations you can use to decorate service interface.

## [BlueberryService]
- Target : Class
- Retention : Source

Annotation [BlueberryService] has no argument to pass(currently). It is used as an entry point indicating target interface is apparently a service set of BLE methods. To configure service, it is necessary to be explicitly declared at the very top of each interface code before you set any further functions.
```kotlin

@BlueberryService // Type before declaring interface.
interface YourBleService {
  // BLE method here.
}

```

## [READ]
- Target : Function
- Retention : Source
- Argument :
  - uuidString / String : UUID value of BLE characteristic 

Annotation [READ] is for BLE `READ` method. It literally request data to slave device and receive it. This annotation has one argument `uuidString`, which means what characteristic is matched with following method. Commonly, across all the other method annotation, `uuidString` must be in form of following regular expression.
  1. [0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}
  2. [0-9a-fA-F]{32}
  3. [0-9a-fA-F]{4}
```kotlin
@BlueberryService
interface YourBleService{
  @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
  fun myReadFunction() : BlueberryReadRequestInfo<string>
}
```

##### TMI : Assuming BLE as a REST API, slave device is the API server and your android application works like a client. [READ] method, in this case, acts like `GET` method in REST API, however, [READ] method cannot send any parameter but only receives data from target device. So, you cannot add any parameter to the example function above `myReadFunction`.

## [WRITE]
- Target : Function
- Retention : Source
- Argument :
  - uuidString / String : UUID value of BLE characteristic
  - checkIsReliable / Boolean : Set it `true` to make following method `reliable write`. Default is `false`.

Annotation [WRITE] is for BLE `WRITE` method. It sends data to slave device but cannot get any complex result. Instead, you'll have status code only. 

It has two arguments.

`uuidString` is identical with one of [READ].

`checkIsReliable` is a bit complicated. First, it is `Boolean`. When you set it `true`, then you can change the following function from simple `WRITE` to `Reliable WRITE`. When it comes to word "`Reliable`", using this function later, it allows checking back transmitted values and atomic execution of one or more transmitted messages.

Let's say you go to Starbucks and order coffee.

In normal `WRITE` method situation :
    
    You : Hey, um... I'd like to order a cup of macchiato.
    Clerk : Yes, sir.
On the other hand, in `Reliable WRITE` method situation :

    You : Macchiato plz.
    Clerk : Are you sure you ordered a macchiato?
    You : That's right.
    Clerk : Yes, sir.

Nailed it. Simple, right?

The default value is `false`. `Reliable WRITE` function is... well, literally reliable. But, it slightly takes some more time.

```kotlin
@BlueberryService
interface YourBleService{
  @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
  fun myWriteFunction(dataToSend : string) : BlueberryWriteRequestInfo
}
```
You can set one parameter of the function. The type of it could be anything. I'll tell you later on [Converter](#Converter) section.

## [WRITE_WITHOUT_RESPONSE]
- Target : Function
- Retention : Source
- Argument :
  - uuidString / String : UUID value of BLE characteristic
  - checkIsReliable / Boolean : Set it `true` to make following method `reliable write`. Default is `false`.

Annotation [WRITE_WITHOUT_RESPONSE] is for BLE `WRITE_WITHOUT_RESPONSE` method. It has almost the same functionality with [WRITE]. But, you cannot receive any result, not even status code. It only sends data to the device and just forget.

```kotlin
@BlueberryService
interface YourBleService {
  @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
  fun myWriteWithoutResponseFunction(dataToSend : string) : BlueberryWriteRequestInfoWithoutResponse
}
```

## [NOTIFY]
- Target : Function
- Retention : Source
- Argument :
  - uuidString / string : UUID value of BLE characteristic
  - endSignal / string : Notification end signal string. Default is `\n`.

Annotation [NOTIFY] is for BLE `NOTIFY` method. Notification is quite different from any other BLE methods. Like a hook, subscribing specific characteristic, you can receive data continously.

Imagine you want to develope a BLE machine measuring your heart rate. When your android application needs to know condition of your blood flow every 10 seconds, or warn you when it drops below a certain level of it, does it have to request [READ] over and over again? Of course not. 

Make a hook. Set a listener. Say your device "`Notify me when it's urgent`".

```kotlin
@BlueberryService
interface YourBleService {
  @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
  fun myNotifyFundtion() : BlueberryNotifyOrIndicateRequestInfo<String>
}
```

Normally, length of buffered data of notification is limited to 32 bytes. It's fine when the size of transmitted data string is less than that but if not, you're gonna receive it partialy multiple times.. For instance, if the slave device send you a long data string, say... of which length is a hundread, they're divided into 4 different packets and transmitted. And of course, you're gonna receive them 4 times. This is definitely not what you've intended. Instead, setting `endSignal` to custom signal string, you'll have complete data once and for all.

## [INDICATE]
- Target : Function
- Retention : Source
- Argument :
  - uuidString / string : UUID value of BLE characteristic
  - endSignal / string : Notification end signal string. Default is `\n`.

Annotation [INDICATE] is for BLE `INDICATE` method. Basically, it has same functionality with [NOTIFY]. The difference is that it's reliable. Like `Reliable Write`, it checks back transmitted value of each indication packet. It is reliable but slightly slow.

```kotlin
@BlueberryService
interface YourBleService {
  @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
  fun myIndicateFundtion() : BlueberryNotifyOrIndicateRequestInfo<String>
}
```

# Converter
By default, available data type to transmit or receive is one of these
  - String
  - Char
  - Double
  - Float
  - Long
  - Int
  - Short
  - Byte
  - Boolean

That's right, these are so called primitive type in `java` / `kotlin`.

Except them, BlueberrySherbet will throw an error. 

But, sometimes you want to receive or send not just string data, but also a complex structure, like `Data Class`. How can you do that?

BlueberrySherbet supports `Data Converter` plguin. First things first, there are currently 4 different converters :
  - converter-gson
  - converter-jackson
  - converter-moshi
  - converter-simple-xml

To install any one of these you can add flollwing lines into your app.gradle dependency :

```gradle
  dependencies {
    ⋮
    implementation "com.github.ApexCaptain.BlueberrySherbet:converter-gson:$blueberry_sherbet_version"      // Gson Converter
    implementation "com.github.ApexCaptain.BlueberrySherbet:converter-jackson:$blueberry_sherbet_version"   // Jackson Converter
    implementation "com.github.ApexCaptain.BlueberrySherbet:converter-moshi:$blueberry_sherbet_version"     // Moshi Converter
    implementation "com.github.ApexCaptain.BlueberrySherbet:converter-simple-xml:$blueberry_sherbet_version"// Simple XML Converter
    ⋮
  }
```

Take `converter-gson` as an example, after you install it you may modify your device class :
```kotlin
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
class ExampleDevice : BlueberryDevice<ExampleService> {

    override fun setServiceImpl() : ExampleService = BlueberryExampleServiceImpl(this)

    override fun setBlueberryConverter() : BlueberryConverter {
      return BlueberryGsonConverter {
        Gson()
          .newBuilder()
          .create()
      }
    }
}
```
This means class `ExampleDevice` now use [Gson](https://github.com/google/gson) as its data converter. And of course you have to install `Gson` too.

Let's say your slave device has a humid-temp sensor. When you request `READ` of a certain characteristic it'll return you current temperature and humidity in forms of `JSON`. Like this :
```JSON
{
  "TEMPERATURE" : 65,
  "HUMIDITY" : 30
}
``` 
That means current temperature is 65 degrees fahrenheit (or 18℃) and relative humidity is 30%.

Now, back to the android studio, let's declare a class named `HumidTemp` like this :
```kotlin
data class HumidTemp(
  @SerializedName("TEMPERATURE")
  val temperature : Int,
  @SerializedName("HUMIDITY")
  val humidity : Int
)
```
Then, add a new function to your service. Assume that its uuid is `aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0201` :

```kotlin
@BlueberryService
interface YourBleService {
  ⋮
  @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0201")
  fun humidTemp() : BlueberryReadRequestInfo<HumidTemp>
  ⋮
}
```

After you scan and connect to it, you can use this method as follows :

```kotlin
class MainActivity : AppCompatActivity() {
  private lateinit var mExampleDevice : ExampleDevice
  ⋮
  fun test() {
    mExampleDevice
      .blueberryService
      .humidTemp()
      .call()
      .enqueue { status, value ->
        Log.d("Test", "$value")
        // ↑ HumidTemp(temperature=65, humidity=30)
      }
  }
  ⋮
}
```

# License

    Licensed under the Apache License, Version 2.0 (the "License")
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[blueberryservice]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-blueberry-service/index.html
[indicate]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-i-n-d-i-c-a-t-e/index.html
[notify]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-n-o-t-i-f-y/index.html
[priority]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-priority/index.html
[read]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-r-e-a-d/index.html
[write]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-w-r-i-t-e/index.html
[write_without_response]: https://apexcaptain.github.io/BlueberrySherbet/annotations/com.gmail.ayteneve93.blueberrysherbetannotations/-w-r-i-t-e_-w-i-t-h-o-u-t_-r-e-s-p-o-n-s-e/index.html
