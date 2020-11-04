package com.dam.chapas.bluetooth;

/**
 * @file BluetoothHelper.java
 * @brief Clase ayudante para las conexiones Bluetooth
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Handler;

import com.dam.chapas.app.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * @class BluetoothHelper
 */
public class BluetoothHelper implements BluetoothDevicesAdapter.ListItemClickListener {

    private BluetoothAdapter btAdapter;
    private ArrayList<BluetoothDevice> devices;
    private BluetoothDevicesAdapter btDevAdapter;
    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    public static final String TAG = "ChapasBT";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket btSocket = null;
    private Activity activity;
    private Handler handler;
    private BluetoothService btService = null;
    private boolean server = false;
    public static final int REQUEST_ENABLE_BT = 1;

    /**
     * @brief Constructor de un BluetoothHelper
     * @param activity  Actividad a usar
     */
    public BluetoothHelper(Activity activity) {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<>();
        btDevAdapter = new BluetoothDevicesAdapter(devices, this);
        btDevAdapter.setDevices(devices);
        if(btAdapter == null) {
            throw new RuntimeException("No se soporta Bluetooth en este dispositivo");
        }
        this.activity = activity;
        this.handler = null;
    }

    /**
     * @brief Establece el manejador cuando se recibe un paquete
     * @param handler   El manejador de paquetes entrantes
     */
    public void setRecvHandler(Handler handler) {
        this.handler = handler;
        if(btService != null) {
            btService.setHandler(handler);
        }
    }

    /**
     * @brief Devuelve si actuamos como servidor
     * @return  ¿Somos el servidor?
     */
    public boolean isServer() {
        return server;
    }

    /**
     * @brief Obtén el servicio Bluetooth
     * @return  El servicio Bluetooth
     */
    public BluetoothService getBluetoothService() {
        return btService;
    }

    /**
     * @brief Establece el socket de conexión con otro móvil
     * @param btSocket  El socket de conexión con otro móvil
     */
    public void setSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;

        try {
            btService = new BluetoothService(btSocket, handler);
            btService.start();

            // Nos hemos conectado, inicia el partido
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "¡Conectado!", Toast.LENGTH_SHORT).show();
                    ((MainActivity)activity).Ir_PartidoLocal(null);
                }
            });

        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @brief Manda una petición para poner el dispositivo visible
     * @param activity  Actividad que recibe el resultado de la petición
     */
    public void setVisible(Activity activity) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        activity.startActivity(discoverableIntent);
    }

    /**
     * @brief Obtén el adaptador para la lista de dispositivos emparejados
     * @return  Dicho adaptador
     */
    public BluetoothDevicesAdapter getDevicesAdapter() {
        return btDevAdapter;
    }

    /**
     * @brief Manda una petición para activar Bluetooth
     * @param activity  Actividad que recibe el resultado de la petición
     */
    public void ensureEnable(Activity activity) {
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * @brief Rellena la lista de dispositivos emparejados
     */
    public void fillDevices() {
        Set<BluetoothDevice> lista_paired = btAdapter.getBondedDevices();
        devices.clear();
        for (BluetoothDevice bt : lista_paired) {
            devices.add(bt);
        }
    }

    /**
     * @brief Crea un hilo para el servidor Bluetooth
     * @throws IOException  Si no se pudo abrir el servidor
     */
    public void startServer() throws IOException {
        if(serverThread != null) return;
        serverThread = new ServerThread(this, btAdapter);
        serverThread.start();
        server = true;
    }

    /**
     * @brief Llamado cuando hacemos click en algún elemento de la lista
     * @param clickedItemIndex  Índice del elemento tocado
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {

        // Conéctate con el dispositivo
        BluetoothDevice device = devices.get(clickedItemIndex);
        Toast.makeText(activity.getApplicationContext(), "Conectando con " + device.getName(), Toast.LENGTH_SHORT).show();
        try {
            if(serverThread != null) {
                serverThread.cancel();  // Para el servidor
                serverThread = null;
            }
            clientThread = new ClientThread(this, device);
            clientThread.start();
            server = false;         // No somos el servidor ahora
        } catch (IOException e) {
            Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @brief Detén las conexiones Bluetooth
     */
    public void stop() {
        if(btService != null) {
            btService.cancel();
            btService = null;
        }
        if(serverThread != null) {
            serverThread.cancel();
            serverThread = null;
        }
        if(clientThread != null) {
            clientThread.cancel();
            clientThread = null;
        }
    }
}
