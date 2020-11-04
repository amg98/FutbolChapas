package com.dam.chapas.game;

/**
 * @file EndState.java
 * @brief Fin de una partida
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.app.MainActivity;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.ShaderProgram;
import com.dam.chapas.physics.RigidBody;

/**
 * @class EndState
 */
public class EndState extends GameState {

    protected final GameData data;
    protected GLRendererImpl renderer;
    private boolean firstText;

    /**
     * @brief Constructor de un EndState
     * @param data      Datos del partido
     * @param renderer  Renderer usado
     */
    public EndState(final GameData data, GLRendererImpl renderer) {
        this.data = data;
        this.renderer = renderer;
        this.firstText = true;

        switch(data.getMatchStatus()) {
            case FIRST_TIME:
                initCenterText(data, "Fin de la 1ª parte");
                break;
            case SECOND_TIME:
                initCenterText(data, "Fin del partido");
                break;
            default: break;
        }

        data.getSoundPlayer().playWhistleSound();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUpdate(ShaderProgram shader) {
        animateCenterText(data);

        if(animState == AnimState.END) {
            if(firstText) {
                switch(data.getMatchStatus()) {
                    case FIRST_TIME:
                        data.getMainActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                data.setTurn(true);
                                data.resetStatus(renderer, true);
                            }
                        });
                        initCenterText(data, "Segundo tiempo");
                        data.setMatchStatus(GameData.MatchStatus.SECOND_TIME);
                        break;
                    case SECOND_TIME:
                        data.setMatchStatus(GameData.MatchStatus.END);
                        data.free();
                        data.getMainActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MainActivity)data.getMainActivity()).Ir_Principal(null);
                            }
                        });
                        break;
                    default: break;
                }
                firstText = false;
            } else {
                data.getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeState();
                        data.getSoundPlayer().playWhistleSound();
                    }
                });
            }
        }
    }

    /**
     * @brief Cambia a ChoosingState
     */
    protected void changeState() {
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
