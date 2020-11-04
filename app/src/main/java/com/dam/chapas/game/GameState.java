package com.dam.chapas.game;

/**
 * @file ChoosingState.java
 * @brief Interfaz para un estado de la partida
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.view.View;

import com.dam.chapas.opengl.ShaderProgram;
import com.dam.chapas.physics.RigidBody;

/**
 * @class GameState
 */
public abstract class GameState {

    public enum AnimState {
        WAIT1,
        FADE_IN,
        WAIT2,
        FADE_OUT,
        END,
    };

    protected AnimState animState;
    protected int animCounter;

    /**
     * @brief Inicializa la animación del texto central
     * @param data  Datos del partido
     * @param text  Texto a mostrar
     */
    public void initCenterText(final GameData data, final String text) {

        this.animState = AnimState.WAIT1;
        this.animCounter = 0;

        data.getMainActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                data.getCenterText().setVisibility(View.VISIBLE);
                data.getCenterText().setTransitionAlpha(0.0f);
                data.getCenterText().setText(text);
            }
        });
    }

    /**
     * @brief Anima el texto central
     * @param data  Datos del partido
     */
    public void animateCenterText(final GameData data) {

        data.getMainActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                switch(animState) {
                    case WAIT1:
                        animCounter ++;
                        if(animCounter >= 120) {
                            animCounter = 0;
                            animState = AnimState.FADE_IN;
                        }
                        break;
                    case FADE_IN:
                        animCounter ++;
                        data.getCenterText().setTransitionAlpha((float) animCounter / 30.0f);
                        if(animCounter >= 30) {
                            animCounter = 0;
                            animState = AnimState.WAIT2;
                        }
                        break;
                    case WAIT2:
                        animCounter ++;
                        if(animCounter >= 60) {
                            animCounter = 0;
                            animState = AnimState.FADE_OUT;
                        }
                        break;
                    case FADE_OUT:
                        animCounter ++;
                        data.getCenterText().setTransitionAlpha(1.0f - (float) animCounter / 30.0f);
                        if(animCounter >= 30) {
                            animCounter = 0;
                            animState = AnimState.END;
                        }
                        break;
                    case END:
                        break;
                }
            }
        });
    }

    /**
     * @brief Se llama una vez por fotograma
     * @param shader    El shader para dibujar los elementos
     */
    public abstract void onUpdate(ShaderProgram shader);

    /**
     * @brief Se llama cuando tocamos en la pantalla
     * @param x     Posición X del dedo
     * @param y     Posición Y del dedo
     */
    public abstract void onDown(float x, float y);

    /**
     * @brief Se llama cuando movemos el dedo por la pantalla
     * @param x Posición X del dedo
     * @param y Posición Y del dedo
     */
    public abstract void onMove(float x, float y);

    /**
     * @brief Se llama cuando soltamos el dedo de la pantalla
     * @param x Posición X del dedo
     * @param y Posición Y del dedo
     */
    public abstract void onUp(float x, float y);

    /**
     * @brief Se llama cuando hacemos scroll por la pantalla
     * @param dx Posición X del dedo
     * @param dy Posición Y del dedo
     */
    public abstract void onScroll(float dx, float dy);

    /**
     * @brief Se llama cuando dos cuerpos colisionan
     * @param b1    Cuerpo 1
     * @param b2    Cuerpo 2
     */
    public abstract void onCollision(RigidBody b1, RigidBody b2);
}
