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



            movementDevice.blueberryService.connectToWiFi(WiFiCredential(
                "KT_GiGA_WiFi_Home_2.4GHz",
                "Dkdlxpspqm93!"
            )).call().byCoroutine()
            movementDevice.blueberryService.getConnectionResult().call().byCoroutine().let {
                Log.d("ayteneve93_test", "$it")
            }






        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->
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


        /*
        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->
                    scanResult.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName == "SherbetTest") {
                            BlueberryScanner.stopScan()
                            exampleDevice = scanResult.interlock(this, ExampleDevice::class.java)
                            exampleDevice.connect()
                            testStringCharacteristic()
                        }
                    }
                }
        )
        */


    }

    private fun testStringCharacteristic() {
        GlobalScope.launch {

            /*
            exampleDevice
                .blueberryService
                .stringRead()
                .call()
                .byCoroutine()
                .let { Log.d(TAG, "$it") }

            exampleDevice
                .blueberryService
                .stringWrite("String Data from Android -- WRITE")
                .call()
                .byCoroutine()
                .let { Log.d(TAG, "$it") }

            exampleDevice
                .blueberryService
                .stringReliableWrite("String Data from Android -- Reliable WRITE")
                .call()
                .byCoroutine()
                .let { Log.d(TAG, "$it") }

            exampleDevice
                .blueberryService
                .stringWriteWithoutResponse("String Data from Android -- WRITE_WITHOUT_RESPONSE")
                .call()
                .enqueue()

            exampleDevice
                .blueberryService
                .stringReliableWriteWithoutResponse("String Data from Android -- Reliable WRITE_WITHOUT_RESPONSE")
                .call()
                .enqueue()
            */



            /*
            exampleDevice
                .blueberryService
                .stringNotifyWithEndSignal()
                .call()
                .enqueue { status, value ->
                    Log.d(TAG, "1 -- Noti")
                }

            exampleDevice
                .blueberryService
                .stringIndicateWithEndSignal()
                .call()
                .enqueue { status, value ->
                    Log.d(TAG, "1 -- Indi")
                }
            */





        }
    }

    companion object  {
        const val TAG = "BlueberryTest"
    }

}
