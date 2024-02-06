package com.example.clientbluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.UUID

class ConnectThread(device: BluetoothDevice,  var status: ((String) -> Unit)? = null): Thread() {
    private val uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"
    private var mSocket: BluetoothSocket? = null

    private val TAG = "Bluetooth"

    init{
        try {
            status?.invoke("Connecting...")
            mSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))
            status?.invoke("Connected")

        }catch (e: IOException){
            status?.invoke("connectFailed")
        }catch (e: SecurityException){
            status?.invoke("connectFailed")
        }
    }

    fun sendMessage(message: String) {
        try {
            mSocket?.outputStream?.write(message.toByteArray())
            Log.d("Bluetooth", "send message $message")
            status?.invoke("Send message success")
        }catch (e: IOException){
            status?.invoke("Send message failed")
            Log.d("Bluetooth", "not send message")
        }
    }

    override fun run() {
        Log.d(TAG, "Begin connect")

        try {
            mSocket?.connect()
        }catch (e: IOException){
//            mSocket?.close()
        }catch (e: SecurityException){
            Log.e(  TAG, "unable to close() ")
            status?.invoke("connectFailed")
        }
        connectFailed();
        return
    }

    private fun connectFailed() {
        Log.d(TAG,"connectFailed")

    }

    fun closeConnection(){
        try {
            mSocket?.close()
        }catch (e: IOException){

        }
    }
}