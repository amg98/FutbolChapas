package com.dam.chapas.bluetooth;

/**
 * @file ServerThread.java
 * @brief Hilo para un servidor Bluetooth
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ServerThread extends Thread {

    private final BluetoothServerSocket mmServerSocket;
    private static final String NAME = "ChapasService";
    private BluetoothHelper helper;

    /**
     * @brief Constructor de ServerThread
     * @param helper        Ayudante de conexiones Bluetooth
     * @param btAdapter     Adaptador Bluetooth
     * @throws IOException  Si no se ha podido crear el canal RF
     */
    public ServerThread(BluetoothHelper helper, BluetoothAdapter btAdapter) throws IOException {
        if(!btAdapter.isEnabled()) {
            throw new RuntimeException("No se ha activado Bluetooth");
        }
        mmServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(NAME, BluetoothHelper.MY_UUID);
        this.helper = helper;
    }

    /**
     * @brief Punto de entrada del hilo del servidor
     */
    public void run() {
        BluetoothSocket socket = null;
        Log.e(BluetoothHelper.TAG, "Server started");

        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(BluetoothHelper.TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                helper.setSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(BluetoothHelper.TAG, "Can't close server socket");
                }
                break;
            }
        }
    }

    /**
     * @brief Detén el hilo del servidor
     */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(BluetoothHelper.TAG, "Could not close the connect socket", e);
        }
    }
}
