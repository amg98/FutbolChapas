package com.dam.chapas.online;

/**
 * @file EndStateOnline.java
 * @brief Fin de una partida
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.game.ChoosingState;
import com.dam.chapas.game.EndState;
import com.dam.chapas.game.GameData;
import com.dam.chapas.opengl.GLRendererImpl;

/**
 * @class EndStateOnline
 */
public class EndStateOnline extends EndState {

    /**
     * @brief Constructor de EndStateOnline
     * @param data      Datos de la partida
     * @param renderer  Renderizador a usar
     */
    public EndStateOnline(GameData data, GLRendererImpl renderer) {
        super(data, renderer);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void changeState() {
        if(MainApplication.getInstance().getBluetoothHelper().isServer()) {
            renderer.changeGameState(new WaitingStateOnline(data, renderer));
        } else {
            renderer.changeGameState(new ChoosingStateOnline(data, renderer));
        }
    }
}
