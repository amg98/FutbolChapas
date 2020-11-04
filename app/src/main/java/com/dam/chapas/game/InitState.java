package com.dam.chapas.game;

/**
 * @file InitState.java
 * @brief Pitido inicial
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.view.View;

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.ShaderProgram;
import com.dam.chapas.physics.RigidBody;
import com.dam.chapas.sound.SoundPlayer;

/**
 * @class InitState
 */
public class InitState extends GameState {

    protected final GameData data;
    protected GLRendererImpl renderer;

    /**
     * @brief Constructor de un InitState
     * @param data      Datos de la partida
     * @param renderer  Renderizador OpenGL
     */
    public InitState(final GameData data, GLRendererImpl renderer) {

        this.data = data;
        this.renderer = renderer;

        initCenterText(data, "¡Comienza el partido!");

        data.getSoundPlayer().playAmbientSound();
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
                    renderer.changeGameState(new ChoosingState(data, renderer));
                    data.getSoundPlayer().playWhistleSound();
                }
            });
        }
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
