package com.dam.chapas.game;

/**
 * @file GoalState.java
 * @brief Un equipo ha metido gol
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.ShaderProgram;
import com.dam.chapas.physics.RigidBody;

/**
 * @class GoalState
 */
public class GoalState extends GameState {

    protected final GameData data;
    protected GLRendererImpl renderer;
    protected int team;

    /**
     * @brief Constructor de un GoalState
     * @param team      Equipo que ha metido gol
     * @param data      Datos del partido
     * @param renderer  Renderer usado
     */
    public GoalState(int team, final GameData data, GLRendererImpl renderer) {
        this.data = data;
        this.renderer = renderer;
        this.team = team;

        if(team == 0) {
            initCenterText(data, "¡Gol del local!");
        } else {
            initCenterText(data, "¡Gol del visitante!");
        }

        data.getSoundPlayer().playCrowdSound();
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
                    data.updateGoals(team, renderer);
                    changeState();
                    data.getSoundPlayer().playWhistleSound();
                }
            });
        }
    }

    /**
     * @brief Cambia el estado de la partida
     */
    protected void changeState() {
        data.setTurn(team == 0);    // Saca el que ha recibido gol
        renderer.changeGameState(new ChoosingState(data, renderer));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDown(float x, float y) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void onMove(float x, float y) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUp(float x, float y) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void onScroll(float dx, float dy) {

    }

    /**
     * @inheritDoc
     */
    public void onCollision(RigidBody b1, RigidBody b2) {

    }
}
