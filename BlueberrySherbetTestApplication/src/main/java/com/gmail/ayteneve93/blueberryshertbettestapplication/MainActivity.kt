package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberryshertbettestapplication.test.CertificationInfo
import io.reactivex.disposables.CompositeDisposable
import com.gmail.ayteneve93.blueberryshertbettestapplication.test.TestDevice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var testDevice : TestDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mCompositeDisposable.add(
            BlueberryScanner.rxStartScan(this)
                .subscribe {
                    if(it.bluetoothDevice.name == "NYSLP19020031P") {
                        testDevice = it.connect(this, TestDevice::class.java)
                        BlueberryScanner.stopScan()
                        nextProgress()
                    }
                }
        )

    }

    fun nextProgress() {
        mCompositeDisposable.dispose()
        val a = CertificationInfo(
            "Uv4OywyiZZhlJDvtm8JCH48WIs03",
            2
        )
        /*
        testDevice.blueberryService.certificate(a).call().also {
            it.enqueue { Log.d("ayteneve93_test", "1 - $it") }
            it.byRx2()
                .subscribe {
                    status, throwable ->
                    Log.d("ayteneve93_test", "2 - $it")
                }
            GlobalScope.launch {
                Log.d("ayteneve93_test", "3 - ${it.byCoroutine()}")
            }

        }

         */
        testDevice.blueberryService.certificate(a).also {
            it.call().enqueue { Log.d("ayteneve93_test", "1 - $it") }
            it.call().byRx2()
                .subscribe {
                    status, throwable ->
                    Log.d("ayteneve93_test", "2 - $it")
                }
            GlobalScope.launch {
                Log.d("ayteneve93_test", "3 - ${it.call().byCoroutine()}")
            }
        }




    }

}
