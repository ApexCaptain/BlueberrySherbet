package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberryshertbettestapplication.movement.MovementDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.movement.WiFiCredential
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.ExampleDevice
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author ayteneve93@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var exampleDevice : ExampleDevice
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->
                    scanResult.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("SherbetTest")) {
                            BlueberryScanner.stopScan()
                            exampleDevice = scanResult.interlock(this, ExampleDevice::class.java)
                            exampleDevice.connect()

                            // testStringCharacteristic()
                            // testIntegerCharacteristic()

                        }
                    }
                }
        )
    }


    private fun testStringCharacteristic() {
        GlobalScope.launch {

            exampleDevice
                .blueberryService
                .stringRead()
                .call()
                .byCoroutine()
                .let { Log.d(TAG, "$it") }


            exampleDevice
                .blueberryService
                .stringWrite("Hello, this is simple string writing.")
                .call()
                .byCoroutine()
                .let { Log.d(TAG, "$it") }


            exampleDevice
                .blueberryService
                .stringReliableWrite("This is reliable writing.")
                .call()
                .byCoroutine()
                .let { Log.d(TAG, "$it") }


            exampleDevice
                .blueberryService
                .stringNotifyWithEndSignal()
                .call()
                .enqueue { _, value ->
                    Log.d(TAG, "Noti with signal -- $value")
                }


            exampleDevice
                .blueberryService
                .stringNotifyWithoutEndSignal()
                .call()
                .enqueue { _, value ->
                    Log.d(TAG, "Noti without signal -- $value")
                }

        }
    }


    private fun testIntegerCharacteristic() {
        GlobalScope.launch {


            exampleDevice
                .blueberryService
                .integerRead()
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }


            exampleDevice
                .blueberryService
                .integerWrite(100)
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }


            exampleDevice
                .blueberryService
                .integerReliableWrite(200)
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG ,"$it")
                }


            exampleDevice
                .blueberryService
                .integerNotifyWithEndSignal()
                .call()
                .enqueue { _, value ->
                    Log.d(TAG, "$value")
                }


            exampleDevice
                .blueberryService
                .integerNotifyWithoutEndSignal()
                .call()
                .enqueue { _, value ->
                    Log.d(TAG, "$value")
                }





        }
    }


    private fun testJsonCharacteristic() {
        GlobalScope.launch {

            exampleDevice
                .blueberryService
                .jsonRead()
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG ,"$it")
                }

        }
    }

    companion object  {
        const val TAG = "BlueberryTest"
    }

}
