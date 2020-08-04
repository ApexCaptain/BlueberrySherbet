package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import io.reactivex.disposables.CompositeDisposable
import com.gmail.ayteneve93.blueberryshertbettestapplication.test.TestDevice

/**
 * @author ayteneve93@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var testDevice : TestDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe {
                    Log.d("ayteneve93_test", it.toString())
                    /*
                    it.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("SleepCare")) {
                            testDevice = it.connect(this, TestDevice::class.java, true)
                            BlueberryScanner.stopScan()
                        }
                    }
                    */

                    /*
                    if(it.bluetoothDevice.name == TestDevice.name) {
                        testDevice = it.connect(this, TestDevice::class.java, true)
                        BlueberryScanner.stopScan()
                    }
                  
                     */
                }
        )

    }



}
