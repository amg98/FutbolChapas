package com.dam.chapas.bluetooth;

/**
 * @file ClientThread.java
 * @brief Hilo de gestión de conexiones como cliente
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * @class ClientThread
 */
public class ClientThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothHelper helper;

    /**
     * @brief Constructor de ClientThread
     * @param helper        Ayudante de conexiones Bluetooth
     * @param device        Dispositivo al que conectarte
     * @throws IOException  Si no hemos podido crear el canal RF
     */
    public ClientThread(BluetoothHelper helper, BluetoothDevice device) throws IOException {

        mmDevice = device;
        mmSocket = device.createRfcommSocketToServiceRecord(BluetoothHelper.MY_UUID);
        this.helper = helper;
    }

    /**
     * @brief Punto de entrada del hilo del cliente
     */
    public void run() {

        // Cancel discovery because it otherwise slows down the connection.
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(BluetoothHelper.TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        helper.setSocket(mmSocket);
    }

    /**
     * @brief Detén el hilo del cliente
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(BluetoothHelper.TAG, "Could not close the client socket", e);
        }
    }
}
