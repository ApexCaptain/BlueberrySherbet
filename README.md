[![JitPack](https://img.shields.io/jitpack/v/github/ApexCaptain/BlueberrySherbet?color=GREEN&label=Blueberry%20Sherbet&style=flat-square&logo=Android%20Studio)](https://jitpack.io/#ApexCaptain/BlueberrySherbet)

# <img src="ReadMeRes/logo.png" alt="Blueberry Sherbet Logo" width="70" height="70">BlueberrySherbet : BLE Master Device Framework for Android Native

# Table of Contents

- [Introduction](#Introduction)
- [Installation](#Installation)
- [Example](#Example)
- [Converter](#Converter)
- [Annotation](#Annotation)
- [License](#License)

# Introduction

BlueberrySherbet is a fast and efficient open source [BLE(Bluetooth Low Energy)](https://wikipedia.org/wiki/Bluetooth_Low_Energy) master device framework for Android Application, which interfaces with various slave devices such as raspberry pi or ESP32 operating your custom embedded bluetooth software. For detailed instruction, please see [Blueberry Sherbet API Documents](https://apexcaptain.github.io/BlueberrySherbet/ "GitHub Pages").

This framework supports [READ], [WRITE], [WRITE_WITHOUT_RESPONSE], [NOTIFY]
and [INDICATE] methods. If you declare a BLE API as Kotlin interface, BlueberrySherbet turns it into an implemented service class file. Each API from the created service can make an asynchronous BLE request to the connected device. Every call can be converted as a form of RxJava2, Coroutine or just simple callback.

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

I'll not explain how the source of slave works. There are already various different frameworks and module supporting BLE. If you're gonna use [nodejs](https://nodejs.org/ko/) as main runtime of slave and want to know how does it work, see [bleno](https://www.npmjs.com/package/bleno).

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

@BlueberryService
interface ExampleService {
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee101")
    fun readSomeString() : BlueberryReadRequestInfo<String>
}
```

### Step 2. Build it.

Build or Rebuild your project. Annotation processor of BlueberrySherbet will automatically generate implemented service file and its name would be `Blueberry{Your Service Interface Name}Impl`. In this case, it's `BlueberryExampleServiceImpl`.

### Step 3. Create Device Class

Make a device class, of which individual instance is matched 1 to 1 with the actual slave device. You can see full code [here](https://github.com/ApexCaptain/BlueberrySherbet/blob/master/app/src/main/java/com/gmail/ayteneve93/blueberryshertbettestapplication/slave/ExampleDevice.kt).

```kotlin
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
class ExampleClass : BlueberryDevice<ExampleService> {

    override fun setServiceImpl() : ExampleService = BlueberryExampleServiceImpl(this)

}
```

Example service extends BlueberryDevice, and it has one generic argument, service type. Service type is the interface that we've just created and run building at step 1 and 2. In this case, `ExampleService` would be right. After that, add a method `setServiceImpl` returning an instance of actually implemented service class, `BlueberryExampleServiceImpl`.

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

- Declare Service Interface
- Build it
- Create Device Class
- Scan and connect where you want to use BLE device.
- Send request and get result.

# License

    Licensed under the Apache License, Version 2.0 (the "License");
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
