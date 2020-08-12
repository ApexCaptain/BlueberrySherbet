package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryConverter
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyDataClassAsGson
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyEnum
import io.reactivex.disposables.CompositeDisposable
import com.gmail.ayteneve93.blueberryshertbettestapplication.test.TestDevice
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
                    it.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("SleepCare")) {
                            testDevice = it.connect(this, TestDevice::class.java, true)
                            BlueberryScanner.stopScan()
                            //test()
                        }
                    }
                }
        )



        button.setOnClickListener {
            test()
        }



    }

    private fun test() {


        testDevice.blueberryService.registerUserWrite(MyDataClassAsGson("SangHun", MyEnum.A)).call().enqueue {
            //Log.d("ayteneve93_test", "$it")
        }

         /*
        testDevice.blueberryService.registerUserRead().call().enqueue { status, value ->
            Log.d("ayteneve93_test", value?:"no")
        }

         */
    }



}
