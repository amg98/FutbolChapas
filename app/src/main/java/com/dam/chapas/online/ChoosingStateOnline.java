package com.dam.chapas.online;

/**
 * @file ChoosingStateOnline.java
 * @brief Elegir una chapa y lanzarla
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.game.ChoosingState;
import com.dam.chapas.game.EndState;
import com.dam.chapas.game.GameData;
import com.dam.chapas.game.GoalState;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.ShaderProgram;

/**
 * @class ChoosingStateOnline
 */
public class ChoosingStateOnline extends ChoosingState {

    /**
     * @brief Constructor de ChoosingStateOnline
     * @param data      Datos de la partida
     * @param renderer  Renderizador a usar
     */
    public ChoosingStateOnline(final GameData data, GLRendererImpl renderer) {

        super(data, renderer);
        final boolean isServer = MainApplication.getInstance().getBluetoothHelper().isServer();
        data.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data.setTurn(!isServer);
            }
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void goEndState() {
        MovePdu pdu = new MovePdu();
        pdu.setExpired(2);
        pdu.send(MainApplication.getInstance().getBluetoothHelper().getBluetoothService());
        renderer.changeGameState(new EndStateOnline(data, renderer));
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void goGoalState(int team) {
        renderer.changeGameState(new GoalStateOnline(team, data, renderer));
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void toggleTurn() {
        data.getSoundPlayer().playWhistleSound();   // Avisa de un cambio de turno
        renderer.changeGameState(new WaitingStateOnline(data, renderer));
        MovePdu pdu = new MovePdu();
        pdu.setExpired(1);
        pdu.send(MainApplication.getInstance().getBluetoothHelper().getBluetoothService());
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void sendShoot(int capID, float impulseX, float impulseZ) {
        MovePdu pdu = new MovePdu();
        pdu.setFrame(data.getTotalTime());
        pdu.setTurnTime(data.getTurnTime());
        pdu.setCapID(capID);
        pdu.setDirectionX(impulseX);
        pdu.setDirectionZ(impulseZ);
        pdu.setExpired(0);
        pdu.send(MainApplication.getInstance().getBluetoothHelper().getBluetoothService());
    }
}
