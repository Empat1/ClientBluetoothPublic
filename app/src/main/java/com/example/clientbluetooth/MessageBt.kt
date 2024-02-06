package com.example.clientbluetooth

data class MessageBt(val type: Int , val sum: String = "null", var rrn: String = "null"){

    fun toExport(): String {
        return "$type $rrn $sum"
    }


}