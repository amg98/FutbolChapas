package com.dam.chapas.online;

/**
 * @file GoalStateOnline.java
 * @brief Un equipo ha metido gol
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.game.ChoosingState;
import com.dam.chapas.game.GameData;
import com.dam.chapas.game.GoalState;
import com.dam.chapas.opengl.GLRendererImpl;

/**
 * @class GoalStateOnline
 */
public class GoalStateOnline extends GoalState {

    /**
     * @brief Constructor de GoalStateOnline
     * @param team      Equipo que ha metido gol (0 = local, 1 = visitante)
     * @param data      Datos de la partida
     * @param renderer  Renderizador a usar
     */
    public GoalStateOnline(int team, GameData data, GLRendererImpl renderer) {
        super(team, data, renderer);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void changeState() {
        boolean isServer = MainApplication.getInstance().getBluetoothHelper().isServer();
        // Si ha metido el local y soy el local -> no es mi turno
        // Si ha metido el local y soy el visitante -> es mi turno
        // Si ha metido el visitante y soy el local -> es mi turno
        // Si ha metido el visitante y soy el visitante -> no es mi turno
        if((team == 0 && !isServer) || (team == 1 && isServer)) {
            renderer.changeGameState(new ChoosingStateOnline(data, renderer));
        } else {
            renderer.changeGameState(new WaitingStateOnline(data, renderer));
        }
    }
}
