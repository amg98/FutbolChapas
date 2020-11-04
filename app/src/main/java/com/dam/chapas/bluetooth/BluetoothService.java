package com.dam.chapas.bluetooth;

/**
 * @file BluetoothService.java
 * @brief Servicio de envío y recepción de datos por Bluetooth
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @class BluetoothService
 */
public class BluetoothService extends Thread {

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_TOAST = 1;

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer;
    private Handler handler;

    /**
     * @brief Constructor de BluetoothService
     * @param socket        Socket de conexión
     * @param handler       Manejador de paquetes recibidos
     * @throws IOException  Si no se pueden obtener los flujos de entrada/salida
     */
    public BluetoothService(BluetoothSocket socket, Handler handler) throws IOException {
        mmSocket = socket;
        mmInStream = socket.getInputStream();
        mmOutStream = socket.getOutputStream();
        this.handler = handler;
    }

    /**
     * @brief Establece el manejador de paquetes recibidos
     * @param handler   Dicho manejador
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * @brief Punto de entrada del hilo del servicio
     */
    public void run() {
        mmBuffer = new byte[1024];
        int numBytes;

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                if(handler != null) {
                    Message readMsg = handler.obtainMessage(MESSAGE_READ, numBytes, -1, mmBuffer);
                    readMsg.sendToTarget();
                }
            } catch (IOException e) {
                Log.d(BluetoothHelper.TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    /**
     * @brief Envía datos a través del socket
     * @param bytes     Datos a enviar
     */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(BluetoothHelper.TAG, "Error occurred when sending data", e);

            // Envía el mensaje de error
            if(handler != null) {
                Message writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }
    }

    /**
     * @brief Detén la ejecución del hilo
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(BluetoothHelper.TAG, "Could not close the connect socket", e);
        }
    }
}
