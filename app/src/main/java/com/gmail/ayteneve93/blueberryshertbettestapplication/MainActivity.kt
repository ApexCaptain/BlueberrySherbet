package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.gmail.ayteneve93.blueberryshertbettestapplication.databinding.ActivityMainBinding
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.Animal
import com.gmail.ayteneve93.converter_simple_xml.BlueberrySimpleXmlConverter
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.ExampleDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.Person
import com.gmail.ayteneve93.blueberryshertbettestapplication.slave.Product
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author ayteneve93@gmail.com
 */
class MainActivity : AppCompatActivity() {

    // https://stackoverflow.com/questions/33937005/monitor-non-beacon-ble-devices-android-beacon-library
    lateinit var binding : ActivityMainBinding
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var exampleDevice : ExampleDevice
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        // Object -> Json
        val objectMapper = ObjectMapper()
        val jsonString = objectMapper.writeValueAsString(Animal("A-Long", "Dog"))

        // Json -> Object
        val obj = objectMapper.readValue(jsonString, Animal::class.java)
        Log.d(TAG, "$obj")

        // Object -> Xml
        /*
        val xmlMapper = XmlMapper()
        val xmlString = xmlMapper.writeValueAsString(obj)
        Log.d(TAG, xmlString)
        */
        /*
        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe { scanResult ->
                    scanResult.bluetoothDevice.name?.let { advertisingName ->
                        if(advertisingName.startsWith("MyDevice"/*"SherbetTest"*/)) {
                            scanResult.onRssiChanged {
                                binding.rssiValue.text = "$it"
                            }
                            scanResult.onDistanceChanged(-55, 4) {
                                binding.distance.text = "$it"
                            }

                            // BlueberryScanner.stopScan()
                            // exampleDevice = scanResult.interlock(this, ExampleDevice::class.java)
                            // exampleDevice.connect()

                            // testStringCharacteristic()
                            // testIntegerCharacteristic()
                            // testGsonCharacteristic()
                            // testSimpleXmlCharacteristic()
                        }
                    }
                }
        )
        */



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
