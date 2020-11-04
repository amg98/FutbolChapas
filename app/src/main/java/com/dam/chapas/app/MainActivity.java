package com.dam.chapas.app;

/**
 * @file MainActivity.java
 * @brief Actividad principal de la aplicación
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.chapas.R;
import com.dam.chapas.bluetooth.BluetoothHelper;
import com.dam.chapas.bluetooth.BluetoothService;
import com.dam.chapas.game.GameData;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.GLSurfaceViewImpl;
import android.os.Handler;

import java.io.IOException;

/**
 * @class MainActivity
 */
public class MainActivity extends Activity {

    private GLRendererImpl renderer = null;

    /**
     * @brief Callback llamado cuando se crea la actividad
     * @param savedInstanceState    Datos de instancia
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepara el singleton
        MainApplication.getInstance().setAssets(getAssets());
        MainApplication.getInstance().setWindowManager(getWindowManager());
        MainApplication.getInstance().setAppContext(getApplicationContext());
        MainApplication.getInstance().setBluetoothHelper(null);

        // Muestra el layout principal
        setContentView(R.layout.activity_main);
    }

    /**
     * @brief Ve a jugar un partido local
     */
    public void Ir_PartidoLocal(View view) {

        // Cambia el layout
        setContentView(R.layout.juegolocal);

        // Obtén los elementos del GUI
        TextView resultText = findViewById(R.id.resultText);
        TextView tiempoTurnoText = findViewById(R.id.tiempoTurnoText);
        TextView tiempoText = findViewById(R.id.tiempoText);
        TextView chutsText = findViewById(R.id.chutsText);
        TextView centerText = findViewById(R.id.centerText);

        // Ponlos en invisible por ahora
        resultText.setVisibility(View.INVISIBLE);
        tiempoTurnoText.setVisibility(View.INVISIBLE);
        tiempoText.setVisibility(View.INVISIBLE);
        chutsText.setVisibility(View.INVISIBLE);
        centerText.setVisibility(View.INVISIBLE);

        // Guarda los elementos del GUI
        GameData.setGuiElements(this, resultText, tiempoTurnoText, tiempoText, chutsText, centerText);

        // Guarda el renderer
        renderer = ((GLSurfaceViewImpl)findViewById(R.id.glSurfaceViewID)).getRenderer();
    }

    /**
     * @brief Ve a jugar un partido online
     */
    public void Ir_online(View view){

        try {

            // Inicializa Bluetooth
            BluetoothHelper btHelper = new BluetoothHelper(this);
            MainApplication.getInstance().setBluetoothHelper(btHelper);
            btHelper.ensureEnable(this);

            // Carga el layout
            setContentView(R.layout.bluetooth);
            RecyclerView mRecyclerViewDevices = findViewById(R.id.recyclerview_main);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerViewDevices.setLayoutManager(layoutManager);
            mRecyclerViewDevices.setHasFixedSize(true);
            mRecyclerViewDevices.setAdapter(btHelper.getDevicesAdapter());

            // Rellena la lista de dispositivos e inicia el servidor
            if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                btHelper.fillDevices();
                btHelper.setVisible(this);
                btHelper.startServer();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @brief Llamado cuando un proceso arroja un resultado en la actividad
     * @param requestCode   Código de la petición
     * @param resultCode    Resultado obtenido
     * @param data          Datos del intent realizado
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si se ha activado Bluetooth...
        BluetoothHelper btHelper = MainApplication.getInstance().getBluetoothHelper();
        if (btHelper != null && requestCode == BluetoothHelper.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth activado correctamente", Toast.LENGTH_SHORT).show();
                try {
                    btHelper.fillDevices();
                    btHelper.setVisible(this);
                    btHelper.startServer();
                } catch (IOException e) {
                    Toast.makeText(this, "Error iniciando servidor", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Bluetooth no se ha podido activar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @brief Ve al menú principal de la aplicación
     */
    public void Ir_Principal(View view) {

        renderer = null;

        // Detén el Bluetooth
        BluetoothHelper btHelper = MainApplication.getInstance().getBluetoothHelper();
        if(btHelper != null) {
            btHelper.stop();
        }
        MainApplication.getInstance().setBluetoothHelper(null);

        // Carga el menú principal
        setContentView(R.layout.principal);
    }

    /**
     * @brief Llamado cuando se desea ir a los créditos
     */
    public void Ir_Creditos(View view) {
        setContentView(R.layout.credits);
    }

    /**
     * @brief Llamado cuando se destruye la actividad
     */
    public void onDestroy() {
        super.onDestroy();

        // Libera el renderer
        if(renderer != null) {
            renderer.free();
        }

        // Detén Bluetooth
        BluetoothHelper btHelper = MainApplication.getInstance().getBluetoothHelper();
        if(btHelper != null) {
            btHelper.stop();
        }
    }
}
