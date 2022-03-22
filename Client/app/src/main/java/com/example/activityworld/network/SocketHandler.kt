package com.example.activityworld.network

import android.util.Log
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

object SocketHandler {

    private const val SERVER_IP = "62.98.205.157"
    private const val SERVER_PORT = 7777

    private lateinit var socketUser: Socket
    private lateinit var outputStream: ObjectOutputStream
    private lateinit var inputStream: ObjectInputStream

    fun startConnection(): Boolean {
        Log.v("SOCKET_HANDLER", "Starting connection to the Server")
        var result = false
        try {
            val socketAddress: SocketAddress = InetSocketAddress(SERVER_IP, SERVER_PORT)
            socketUser = Socket()
            Log.v("SOCKET_HANDLER", "Socket Created")
            socketUser.connect(socketAddress, 10000)
            Log.v("SOCKET_HANDLER", "Socket Connected")
            outputStream = ObjectOutputStream(socketUser.getOutputStream())
            Log.v("SOCKET_HANDLER", "outputStream created")
            inputStream = ObjectInputStream(socketUser.getInputStream())
            Log.v("SOCKET_HANDLER", "inputStream created")
            Log.v("SOCKET_HANDLER", "Connection Started")

            result = true
        } catch (e: IOException) {
            e.printStackTrace()
            //Log.v("SOCKET_HANDLER_CATCH", "${e.printStackTrace()}")
        }

        return result
    }

    fun sendString(string: String) {
        Log.v("SOCKET_HANDLER", "String sent: $string")
        outputStream.writeUTF(string)
        outputStream.flush()
    }

    fun sendInt(num: Int) {
        Log.v("SOCKET_HANDLER", "Int sent: $num")
        outputStream.writeInt(num)
        outputStream.flush()
    }

    fun sendLong(num: Long) {
        Log.v("SOCKET_HANDLER", "Long sent: $num")
        outputStream.writeLong(num)
        outputStream.flush()
    }

    fun sendBoolean(boolean: Boolean) {
        Log.v("SOCKET_HANDLER", "Long sent: $boolean")
        outputStream.writeBoolean(boolean)
        outputStream.flush()
    }

    fun sendDouble(num: Double) {
        Log.v("SOCKET_HANDLER", "Long sent: $num")
        outputStream.writeDouble(num)
        outputStream.flush()
    }


    fun readBoolean(): Boolean {
        return inputStream.readBoolean()
    }

    fun readInt(): Int {
        return inputStream.readInt()
    }

    fun readString(): String {
        return inputStream.readUTF()
    }

    fun readLong(): Long {
        return inputStream.readLong()
    }


    fun closeConnection() {
        try {
            socketUser.close()
            inputStream.close()
            outputStream.close()
            Log.v("SOCKET_HANDLER", "connection Closed!")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.v("SOCKET_HANDLER_CATCH", "${e.printStackTrace()}")
        }
    }
}