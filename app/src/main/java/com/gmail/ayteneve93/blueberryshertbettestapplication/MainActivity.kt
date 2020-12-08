package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.ExampleDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.SimpleData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.GlobalScope
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
                        if(advertisingName == "SherbetTest") {
                            BlueberryScanner.stopScan()
                            exampleDevice = scanResult.interlock(this, ExampleDevice::class.java)
                            exampleDevice.connect()
                            testStringCharacteristic()
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

        }
    }

    companion object  {
        const val TAG = "BlueberryTest"
    }

}
