package com.gmail.ayteneve93.blueberrysherbetcore.scanner

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.net.MacAddress
import android.os.Build
import android.util.Log
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetcore.R
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import com.gun0912.tedpermission.rx2.TedPermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs
import kotlin.math.pow

object BlueberryScanner {

    val isScanning = ObservableField(false)

    private val mCompositeDisposable = CompositeDisposable()
    private val getBluetoothAdapter = { context : Context -> (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter }

    private var mBluetoothScanCallback : ScanCallback? = null

    var bleScanPermissionRequestTitle : String? = null
    var bleScanPermissionRequestMessage : String? = null

    fun scanByAddress(context : Context, macAddress : MacAddress) = scanByAddress(context, macAddress.toString())
    fun scanByAddress(context : Context, macAddress : ByteArray) = scanByAddress(context, macAddress.toString())
    fun scanByAddress(context : Context, macAddress : String) = BlueberryScanResult(getBluetoothAdapter(context).getRemoteDevice(macAddress))


    fun rxStartScan(context : Context) : Observable<BlueberryScanResult> = rxStartScanImp(context).subscribeOn(Schedulers.io())
    fun rxStartScan(context : Context, filters : MutableList<ScanFilter>, scanSettings: ScanSettings) = rxStartScanImp(context, filters, scanSettings).subscribeOn(Schedulers.io())
    private fun rxStartScanImp(context : Context, filters : MutableList<ScanFilter>? = null, scanSettings : ScanSettings? = null) : Observable<BlueberryScanResult> = Observable.create { observableEmitter ->

        if(isScanning.get()!!) {
            val message = "${this::class.java.simpleName} is Already on Scanning State"
            BlueberryLogger.w(message)
            observableEmitter.onError(IllegalStateException(message))
            return@create
        }
        val permissions = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        mCompositeDisposable.add(
            TedPermission.create()
                .setRationaleTitle(bleScanPermissionRequestTitle ?: context.getString(R.string.ble_scan_permission_request_title))
                .setRationaleMessage(bleScanPermissionRequestMessage ?: context.getString(R.string.ble_scan_permission_request_message))
                .setPermissions(*(permissions.toTypedArray()))
                .request()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        isScanning.set(true)
                        val blueberryScanResults = ArrayList<BlueberryScanResult>()
                        mBluetoothScanCallback = object : ScanCallback() {
                            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                                super.onScanResult(callbackType, result)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return

                                if(!getBluetoothAdapter(context).isEnabled) getBluetoothAdapter(context).enable()
                                result?.device?.let { bluetoothDevice ->
                                    val deviceInfoString = "\nMac Address : ${bluetoothDevice.address} ${if(bluetoothDevice.name != null) "\nAdvertising Name : ${bluetoothDevice.name}" else ""}\nRssi Signal Value : ${result.rssi}"
                                    var blueberryScanResult = blueberryScanResults.find { blueberryScanResult -> blueberryScanResult.bluetoothDevice.address == bluetoothDevice.address }
                                    if(blueberryScanResult == null) {
                                        blueberryScanResult = BlueberryScanResult(bluetoothDevice)
                                        val isConnectible : Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) result.isConnectable
                                        else result.scanRecord?.advertiseFlags.let { advertisingFlag -> advertisingFlag != null && advertisingFlag.and(2) == 2 }
                                        if(isConnectible) {
                                            BlueberryLogger.i("New Ble Device is Found. $deviceInfoString")
                                            blueberryScanResults.add(blueberryScanResult)
                                            observableEmitter.onNext(blueberryScanResult)
                                        }
                                    } else {
                                        BlueberryLogger.v("Ble Device Info is Updated. $deviceInfoString")
                                        blueberryScanResult.updateDevice(bluetoothDevice)
                                    }

                                    blueberryScanResult.rssi.set(result.rssi)

                                }
                            }
                        }
                        if(filters != null && scanSettings != null) getBluetoothAdapter(context).bluetoothLeScanner.startScan(filters, scanSettings, mBluetoothScanCallback)
                        else getBluetoothAdapter(context).bluetoothLeScanner.startScan(mBluetoothScanCallback)
                        mCompositeDisposable.clear()
                    },
                    {
                        BlueberryLogger.e("Ble Scanning Error Occurred", it)
                        observableEmitter.onError(it)
                    }
                )
        )

    }

    fun stopScan(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) return
        if(!isScanning.get()!!) return
        isScanning.set(false)
        getBluetoothAdapter(context).bluetoothLeScanner.stopScan(mBluetoothScanCallback)
        mBluetoothScanCallback = null
    }

}