package com.gmail.ayteneve93.blueberryshertbettestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.scanner.BlueberryScanner
import com.gmail.ayteneve93.blueberryshertbettestapplication.test.CertificationInfo
import io.reactivex.disposables.CompositeDisposable
import com.gmail.ayteneve93.blueberryshertbettestapplication.test.TestDevice


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
        testDevice.blueberryService.certificate(CertificationInfo(
            "Uv4OywyiZZhlJDvtm8JCH48WIs03",
            2
        )).call { resultCode, any ->
            testDevice.blueberryService.readSysconfInfo().call { status, data ->
                Log.d("ayteneve93_test", "status : $status")
                Log.d("ayteneve93_test", data?.toString()?:"null Data")
            }
        }
    }

}
