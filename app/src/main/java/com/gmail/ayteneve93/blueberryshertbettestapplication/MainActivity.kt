package com.gmail.ayteneve93.blueberryshertbettestapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberryshertbettestapplication.databinding.ActivityMainBinding
import com.gmail.ayteneve93.blueberryshertbettestapplication.mask.MaskDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.Animal
import com.gmail.ayteneve93.converter_simple_xml.BlueberrySimpleXmlConverter
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.ExampleDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.Person
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.Product
import com.gmail.ayteneve93.converter_moshi.BlueberryMoshiConverter
import com.squareup.moshi.Moshi
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author ayteneve93@gmail.com
 */
@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {

    // https://stackoverflow.com/questions/33937005/monitor-non-beacon-ble-devices-android-beacon-library
    lateinit var binding : ActivityMainBinding
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var exampleDevice : ExampleDevice
    private lateinit var maskDevice : MaskDevice
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        // Object -> Json
        /*
        val objectMapper = ObjectMapper()
        val jsonString = objectMapper.writeValueAsString(Animal("A-Long", "Dog"))
        */

        // Json -> Object
        /*
        val obj = objectMapper.readValue(jsonString, Animal::class.java)
        Log.d(TAG, "$obj")
        */

        // Object -> Xml
        /*
        val xmlMapper = XmlMapper()
        val xmlString = xmlMapper.writeValueAsString(obj)
        Log.d(TAG, xmlString)
        */


        
        /*
            Context context = requireContext().getApplicationContext();
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        */
        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->

                    scanResult.bluetoothDevice.name?.let { advertisingName ->

                        if(advertisingName.startsWith("USY")) {
                            Log.d("ayteneve93_test", advertisingName)
                            exampleDevice = scanResult.interlock(this, ExampleDevice::class.java, false)
                            exampleDevice.connect()

//                            exampleDevice.blueberryService.openDoorLock(
//
//                            )

                        }

//                        if(advertisingName.startsWith("SleepCare")) {
//                            BlueberryScanner.stopScan(this@MainActivity)
//                            maskDevice = scanResult.interlock(this, MaskDevice::class.java)
//                            maskDevice.connect()
//                            BlueberryScanner.stopScan(this@MainActivity)
//
//                            /*
//                            maskDevice.blueberryService.indicateFromRPI().call().enqueue { _, value ->
//                                Log.d("ayteneve93_test", "$value")
//                            }
//                            */
//                        }

                    }

                    /*
                    scanResult.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("SherbetTest")) {

                            BlueberryScanner.stopScan()
                            exampleDevice = scanResult.interlock(this, ExampleDevice::class.java)
                            exampleDevice.connect()
                            BlueberryScanner.stopScan()
                            testStringCharacteristic()
                            // testIntegerCharacteristic()
                            // testGsonCharacteristic()
                            // testMoshiCharacteristic()
                            // testSimpleXmlCharacteristic()
                        }
                    }
                    */

                }
        )




    }


    private fun testStringCharacteristic() {
        exampleDevice.blueberryService.stringRead().call().enqueue { status, value ->

        }
        mCompositeDisposable.add(
            exampleDevice
                .blueberryService
                .stringRead()
                .call()
                .byRx2()
                .subscribe { result, error ->

                }
        )
        GlobalScope.launch {
            exampleDevice
                .blueberryService
                .stringRead()
                .call()
                .byCoroutine()
                .let { Log.d(TAG, "$it") }


            /*
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
            */

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


    private fun testGsonCharacteristic() {
        GlobalScope.launch {

            exampleDevice
                .blueberryService
                .gsonRead()
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG ,"$it")
                }

            exampleDevice
                .blueberryService
                .gsonWrite(Person("Steve", 38))
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }

            exampleDevice
                .blueberryService
                .gsonReliableWrite(Person("Amanda", 21))
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }

            exampleDevice
                .blueberryService
                .gsonNotifyWithEndSignal()
                .call()
                .enqueue { _, value ->
                    Log.d(TAG, "$value")
                }

        }
    }

    private fun testMoshiCharacteristic() {
        GlobalScope.launch {

            exampleDevice
                .blueberryService
                .moshiRead()
                .apply {
                    setConverter(BlueberryMoshiConverter())
                }
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }

        }
    }

    private fun testSimpleXmlCharacteristic() {
        GlobalScope.launch {

            exampleDevice
                .blueberryService
                .simpleXmlRead()
                .apply {
                    setConverter(BlueberrySimpleXmlConverter())
                }
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }

            exampleDevice
                .blueberryService
                .simpleXmlWrite(Product("Iphone", 2000))
                .apply {
                    setConverter(BlueberrySimpleXmlConverter())
                }
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }

            exampleDevice
                .blueberryService
                .simpleXmlReliableWrite(Product("IPad", 1300))
                .apply {
                    setConverter(BlueberrySimpleXmlConverter())
                }
                .call()
                .byCoroutine()
                .let {
                    Log.d(TAG, "$it")
                }

            exampleDevice
                .blueberryService
                .simpleXmlNotifyWithEndSignal()
                .apply {
                    setConverter(BlueberrySimpleXmlConverter())
                }
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
