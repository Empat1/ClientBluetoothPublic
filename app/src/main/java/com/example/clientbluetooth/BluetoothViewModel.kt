package com.example.clientbluetooth

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {
    private var bluetoothManager: BluetoothManager=  application.getSystemService(BluetoothManager::class.java)
    var bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

    val androidBluetoothController = AndroidBluetoothController(getApplication())

    var status: ((String) -> Unit)? = null
        set(value) {
            androidBluetoothController.status = value
            field = value
        }

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        androidBluetoothController.scannedDevices,
        androidBluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun startScan() {
        androidBluetoothController.startDiscovery()
    }

    fun stopScan() {
        androidBluetoothController.stopDiscovery()
    }

    fun sendMessage(message: String){
        androidBluetoothController.sendMessage(message)
    }

    fun sendMessage(messageBt: MessageBt){
        if(messageBt.rrn == "" || messageBt.rrn == "null"){
            messageBt.rrn = "00" + (100000..999999).random()
        }
        androidBluetoothController.sendMessage(messageBt.toExport())
    }

    fun connect(mac: String) {
        androidBluetoothController.connect(mac)
    }

    data class BluetoothUiState(
        val scannedDevices: List<BluetoothDevice> = emptyList(),
        val pairedDevices: List<BluetoothDevice> = emptyList(),
    )
}