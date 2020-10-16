package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryGsonConverter
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryMoshiConverter
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.ExampleDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyDataClass2
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import io.reactivex.disposables.CompositeDisposable
import javax.crypto.Cipher

/**
 * @author ayteneve93@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var exampleDevice : ExampleDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        val converter = BlueberryMoshiConverter(Moshi.Builder().build())//BlueberryGsonConverter(Gson())
        val md = MyDataClass2("qwe")
        val s = converter.stringify(md, MyDataClass2::class.java)
        Log.d("ayteneve93_test", s)
        */

        /*
        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe {
                    it.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("SleepCare")) {
                            exampleDevice = it.interlock(this, ExampleDevice::class.java)
                            exampleDevice.connect()
                            GlobalScope.launch {


                            }
                        }


                    }
                }
        )
        */





    }



}
