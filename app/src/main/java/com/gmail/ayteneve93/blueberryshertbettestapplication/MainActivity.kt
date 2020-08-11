package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryConverter
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyDataClassAsGson
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyEnum
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
        //test()


        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe {
                    it.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("SleepCare")) {
                            testDevice = it.connect(this, TestDevice::class.java, true)
                            BlueberryScanner.stopScan()
                        }
                    }
                }
        )



    }

    private fun test() {
        /*
        val tmp = MyDataClassAsGson("SangHun", MyEnum.A)
        val tmp = MyDataClassAsGson("SangHun", "Lee")
        val converter = BlueberryConverter()
            .addGsonAdapter(MyEnum::class.java, BlueberryConverter.GsonAdapter<MyEnum>()
                .setSerializer { src, typeOfSrc, context -> context!!.serialize(src!!.name) }
                .setDeserializer { json, typeOfConversion, context -> MyEnum.fromString(json.toString()) }
            )

        var str = converter.convertObjectToString(tmp)
        Log.d("ayteneve93_test", str)
        */

    }



}
