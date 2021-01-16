package com.example.smartrestaurantapp.model;

import android.bluetooth.BluetoothSocket;

import java.io.Serializable;


public class SocketSingleton implements Serializable {
    private static BluetoothSocket socket;

    public SocketSingleton(BluetoothSocket socket){
        this.socket = socket;
    }

    public static void setSocket(BluetoothSocket socketpass){
        SocketSingleton.socket=socketpass;
    }

    public static BluetoothSocket getSocket(){
        return SocketSingleton.socket;
        //return socket;
    }
}
