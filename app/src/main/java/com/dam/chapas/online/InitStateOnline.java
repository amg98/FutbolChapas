package com.dam.chapas.online;

/**
 * @file InitStateOnline.java
 * @brief Pitido inicial
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.game.ChoosingState;
import com.dam.chapas.game.GameData;
import com.dam.chapas.game.InitState;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.ShaderProgram;

/**
 * @class InitStateOnline
 */
public class InitStateOnline extends InitState {

    /**
     * @brief Constructor de InitStateOnline
     * @param data      Datos de la partida
     * @param renderer  Renderizador a usar
     */
    public InitStateOnline(GameData data, GLRendererImpl renderer) {
        super(data, renderer);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUpdate(ShaderProgram shader) {

        animateCenterText(data);

        if(animState == AnimState.END) {
            data.getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(MainApplication.getInstance().getBluetoothHelper().isServer()) {
                        renderer.changeGameState(new ChoosingStateOnline(data, renderer));
                    } else {
                        renderer.changeGameState(new WaitingStateOnline(data, renderer));
                    }
                    data.getSoundPlayer().playWhistleSound();
                }
            });
        }
    }
}
