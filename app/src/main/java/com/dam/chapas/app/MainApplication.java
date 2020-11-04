package com.dam.chapas.app;

/**
 * @file MainApplication.java
 * @brief Singleton de la aplicación
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.WindowManager;

import com.dam.chapas.bluetooth.BluetoothHelper;
import com.dam.chapas.opengl.MatrixSystem;

/**
 * @class MainApplication
 */
public class MainApplication extends Application {

    private static MainApplication app = null;
    private AssetManager assets;
    private WindowManager windowManager;
    private Context appContext;
    private MatrixSystem mtx;
    private BluetoothHelper btHelper;

    /**
     * @brief Obtén el gestor de conexiones Bluetooth
     * @return  El gestor de conexiones Bluetooth
     */
    public BluetoothHelper getBluetoothHelper() {
        return btHelper;
    }

    /**
     * @brief Guarda el gestor de conexiones Bluetooth
     * @param btHelper  El nuevo gestor de conexiones Bluetooth
     */
    public void setBluetoothHelper(BluetoothHelper btHelper) {
        this.btHelper = btHelper;
    }

    /**
     * @brief Constructor de MainApplication
     */
    private MainApplication() {
        mtx = new MatrixSystem();
    }

    /**
     * @brief Obtén la instancia de MainApplication
     * @return  La instancia de este Singleton
     */
    public static MainApplication getInstance() {

        if(app == null) {
            app = new MainApplication();
        }

        return app;
    }

    /**
     * @brief Establece el contexto de la aplicación
     * @param ctx   El contexto de la aplicación
     */
    public void setAppContext(Context ctx) {
        appContext = ctx;
    }

    /**
     * @brief Obtén el contexto de la aplicación
     * @return  El contexto de la aplicación
     */
    public Context getAppContext() {
        return appContext;
    }

    /**
     * @brief Establece el gestor de assets
     * @param assets    El gestor de assets a usar
     */
    public void setAssets(AssetManager assets) {
        this.assets = assets;
    }

    /**
     * @brief Obtén el gestor de assets
     * @return  El gestor de assets actual
     */
    public AssetManager getAssets() {
        return this.assets;
    }

    /**
     * @brief Establece el gestor de ventanas
     * @param windowManager El gestor de ventanas
     */
    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    /**
     * @brief Obtén el gestor de ventanas
     * @return  El gestor de ventanas actual
     */
    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * @brief Obtén el conjunto de matrices para dibujado 3D
     * @return  El conjunto de matrices
     */
    public MatrixSystem getMatrixSystem() {
        return this.mtx;
    }
}
