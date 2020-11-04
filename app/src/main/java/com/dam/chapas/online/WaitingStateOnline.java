package com.dam.chapas.online;

/**
 * @file WaitingStateOnline.java
 * @brief Esperar el movimiento del otro jugador
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.bluetooth.BluetoothService;
import com.dam.chapas.game.ChoosingState;
import com.dam.chapas.game.GameData;
import com.dam.chapas.game.GameState;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.Mesh;
import com.dam.chapas.opengl.ShaderProgram;
import com.dam.chapas.physics.RigidBody;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @class WaitingStateOnline
 */
public class WaitingStateOnline extends ChoosingState {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_READ:
                    MovePdu pdu = new MovePdu();
                    pdu.recv((byte[]) msg.obj);
                    pduQueue.add(pdu);
                    break;
                case BluetoothService.MESSAGE_TOAST:
                    Toast.makeText(MainApplication.getInstance().getAppContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private Queue<MovePdu> pduQueue = new LinkedList<>();

    /**
     * @brief Constructor de WaitingStateOnline
     * @param data      Datos de la partida
     * @param renderer  Renderizador a usar
     */
    public WaitingStateOnline(final GameData data, GLRendererImpl renderer) {

        super(data, renderer);
        final boolean isServer = MainApplication.getInstance().getBluetoothHelper().isServer();
        data.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data.setTurn(isServer);
            }
        });

        initCenterText(data, "Esperando...");
        animState = AnimState.FADE_IN;
        animCounter = 0;

        MainApplication.getInstance().getBluetoothHelper().setRecvHandler(handler);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUpdate(ShaderProgram shader) {

        data.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(animState) {
                    case FADE_IN:
                        animCounter ++;
                        data.getCenterText().setTransitionAlpha((float) animCounter / 20.0f);
                        if(animCounter >= 20) {
                            animCounter = 0;
                            animState = AnimState.FADE_OUT;
                        }
                        break;
                    case FADE_OUT:
                        animCounter ++;
                        if(animCounter >= 20) {
                            data.getCenterText().setTransitionAlpha(1.0f - (float) animCounter / 20.0f);
                            animCounter = 0;
                            animState = AnimState.FADE_IN;
                        }
                        break;
                }
            }
        });

        // Haz que el tiempo pase
        final boolean moving = data.getWorld().isMoving();
        data.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(!moving) {
                    data.tickTurnTime(false);
                }

                data.tickTime();
            }
        });

        // Comprobar gol
        if(moving) {
            float[] ballPos = data.getBall().getPosition();
            if(ballPos[0] >= -0.83f && ballPos[0] <= 0.83f && ballPos[2] >= 5.4f) {
                // Gol para el equipo local
                renderer.changeGameState(new GoalStateOnline(1, data, renderer));
            } else if(ballPos[0] >= -0.83f && ballPos[0] <= 0.83f &&  ballPos[2] <= -5.4f) {
                // Gol para el equipo visitante
                renderer.changeGameState(new GoalStateOnline(0, data, renderer));
            }
        } else {
            while(!pduQueue.isEmpty()) {
                final MovePdu pdu = pduQueue.poll();

                // Actualizar chuts
                if(pdu.getShoots() > 0) {
                    data.getMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            data.setShoots(pdu.getShoots());
                        }
                    });
                }

                // Realiza un movimiento
                else if(pdu.getExpired() == 0) {
                    Mesh cap = data.getKeeper();
                    if(pdu.getCapID() != -1) {
                        cap = data.getCaps()[pdu.getCapID()];
                    }

                    float[] impulse = new float[3];
                    impulse[0] = pdu.getDirectionX();
                    impulse[1] = 0.0f;
                    impulse[2] = pdu.getDirectionZ();
                    cap.getRigidBody().applyImpulse(impulse);

                    data.setTotalTime(pdu.getFrame());
                    data.setTurnTime(pdu.getTurnTime());
                    break;      // Hay que esperar que el mundo se quede quieto de nuevo
                }

                // Final de un tiempo (ir a EndStateOnline)
                else if(pdu.getExpired() == 2) {
                    renderer.changeGameState(new EndStateOnline(data, renderer));
                }

                // Ahora es mi turno
                else if(pdu.getExpired() == 1) {
                    renderer.changeGameState(new ChoosingStateOnline(data, renderer));
                }
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDown(float x, float y) { }

    /**
     * @inheritDoc
     */
    @Override
    public void onMove(float x, float y) { }

    /**
     * @inheritDoc
     */
    @Override
    public void onUp(float x, float y) { }
}
