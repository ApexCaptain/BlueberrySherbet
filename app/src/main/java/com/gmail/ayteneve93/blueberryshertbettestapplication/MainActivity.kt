package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryConverter
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.ExampleDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyDataClassAsGson
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyEnum
import io.reactivex.disposables.CompositeDisposable
import com.gmail.ayteneve93.blueberryshertbettestapplication.test.TestDevice
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
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
                .subscribe {
                    it.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName == "MyDevice") {
                            exampleDevice = it.connect(this, ExampleDevice::class.java)
                            exampleDevice.addOnServiceDiscoveredListener {
                                Log.d("ayteneve93_test", "discovered")
                                GlobalScope.launch {
                                    val s = exampleDevice.blueberryService.beGreetedFromDevice().call().byCoroutine()
                                    Log.d("ayteneve93_test", s.value?:"no Result")
                                }
                            }
                            BlueberryScanner.stopScan()
                        }
                    }
                }
        )



    }



}
