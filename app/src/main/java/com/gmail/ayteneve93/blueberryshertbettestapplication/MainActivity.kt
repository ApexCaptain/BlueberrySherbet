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

    private lateinit var movementDevice : MovementDevice

    private fun testMovement() {
        GlobalScope.launch {

            Log.d("ayteneve93_test", "launch")

            /*
            movementDevice.blueberryService.getWiFiStatus().call().byCoroutine().let {
                Log.d("ayteneve93_test", "$it")
            }
            */

            /*
            movementDevice.blueberryService.setDataSendingState(true).call().byCoroutine()
            movementDevice.blueberryService.setDataSendingState(false).call().byCoroutine()
            */
            /*
            movementDevice.blueberryService.onDataSendingInitiated().call().byRx2().subscribe {
                Log.d("ayteneve93_test", "$it")
            }
            */
            /*
            movementDevice.blueberryService.connectionStatus().call().byRx2().subscribe {
                Log.d("ayteneve93_test", "$it")
            }

            delay(15000)
            movementDevice.disconnect()
            delay(5000)
            movementDevice.connect()
            Log.d("ayteneve93_test", "reconnect")

            movementDevice.blueberryService.connectionStatus().call().byRx2().subscribe {
                Log.d("ayteneve93_test", "$it")
            }
            */

            /*
            movementDevice.blueberryService.scanWiFi().call().byCoroutine().let {
                it.value?.forEach { each ->
                    Log.d("ayteneve93_test", each.toString())
                }
            }
            */



            /*
            movementDevice.blueberryService.connectToWiFi(WiFiCredential(
                "KT_GiGA_WiFi_Home_2.4GHz",
                "Dkdlxpspqm93!"
            )).call().byCoroutine()
            movementDevice.blueberryService.getConnectionResult().call().byCoroutine().let {
                Log.d("ayteneve93_test", "$it")
            }
            */






        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->
                    Log.d("ayteneve93_test", "$scanResult")
                    scanResult.bluetoothDevice.name?.let { advertisingName ->
                        Log.d("ayteneve93_test", advertisingName)
                        if(advertisingName.startsWith("MVMT_")) {
                            Log.d("ayteneve93_test", scanResult.bluetoothDevice.address)
                            BlueberryScanner.stopScan()
                            movementDevice = scanResult.interlock(this, MovementDevice::class.java)
                            movementDevice.connect()
                            testMovement()
                        }
                    }
                }
        )

         */



        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->
                    scanResult.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("SherbetTest")) {
                            BlueberryScanner.stopScan()
                            exampleDevice = scanResult.interlock(this, ExampleDevice::class.java)
                            exampleDevice.connect()

                            // testStringCharacteristic()
                            testIntegerCharacteristic()

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



    companion object  {
        const val TAG = "BlueberryTest"
    }

}
