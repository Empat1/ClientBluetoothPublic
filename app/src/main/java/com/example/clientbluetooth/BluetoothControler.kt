package com.example.clientbluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Message
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AndroidBluetoothController(
    val context: Context,

) {
    var status: ((String) -> Unit)? = null
        set(value) {
            connectThread?.status = value
            field = value
        }

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var connectThread: ConnectThread?= null

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    val foundDeviceReceiver = FoundDeviceReceiver{ device ->
        _scannedDevices.update { devices ->
            if (device in devices) devices else devices + device
        }
    }


    init {
        updatePairedDevices()
    }

    fun startDiscovery() {
//        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
//            return
//        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    private fun updatePairedDevices() {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }


    fun findDevice(mac: String): BluetoothDevice?{
        val pait = _pairedDevices.value.find { it.address == mac }
        if(pait != null) return pait
        return  _scannedDevices.value.find { it.address == mac }
    }

    fun connect(mac: String){
        connect(findDevice(mac))
    }

    fun connect(device: BluetoothDevice?){
        if(device == null)
            return

        if(bluetoothAdapter?.isEnabled == true){
            connectThread = ConnectThread(device, status)
            connectThread?.start()
        }
    }

    fun closeConnection(){
        connectThread?.closeConnection()
    }

    fun sendMessage(message: String){
        connectThread?.sendMessage(message)
    }
}