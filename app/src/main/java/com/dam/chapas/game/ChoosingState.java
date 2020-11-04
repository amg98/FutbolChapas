package com.dam.chapas.game;

/**
 * @file ChoosingState.java
 * @brief Elegir una chapa y lanzarla
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.graphics.Point;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.opengl.Camera;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.MatrixSystem;
import com.dam.chapas.opengl.Mesh;
import com.dam.chapas.opengl.ShaderProgram;
import com.dam.chapas.physics.RigidBody;
import com.dam.chapas.physics.VerticalCylinderBody;

/**
 * @class ChoosingState
 */
public class ChoosingState extends GameState {

    private static final float MAX_ARROW_MAGNITUDE = 0.4f;
    private static final float ARROW_MAGNITUDE_MULT = 1.25f;
    private static final float IMPULSE_MULTIPLIER = 21.0f;
    private static final float PASS_RANGE = 1.0f;

    protected GameData data;
    protected GLRendererImpl renderer;
    private final float SCROLL_SPEED_X = 9.0f;
    private final float SCROLL_SPEED_Y = 6.0f;
    private Mesh arrow;
    private boolean lastMoving;
    private RigidBody ballPosessor;
    private Mesh selectedCap = null;
    private int selectedCapID;
    private float touchPointX, touchPointY;

    /**
     * @brief Ve al final de una parte del juego
     */
    protected void goEndState() {
        renderer.changeGameState(new EndState(data, renderer));
    }

    /**
     * @brief Ve al estado de gol
     * @param team  0 = equipo local mete gol, 1 = equipo visitante mete gol
     */
    protected void goGoalState(int team) {
        renderer.changeGameState(new GoalState(team, data, renderer));
    }

    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {

            if(!data.getWorld().isMoving()) {

                // Si se ha acabado el tiempo de turno...
                if(data.tickTurnTime(false)) {
                    toggleTurn();
                    synchronized (this) {
                        this.notify();
                    }
                    return;
                }
            }

            // Resta al reloj del partido
            if(data.tickTime()) {

                // Si se ha acabado el tiempo...
                goEndState();
                synchronized (this) {
                    this.notify();
                }
                return;
            }

            // Comprueba si se ha metido un gol
            boolean moving = data.getWorld().isMoving();
            if(moving) {
                float[] ballPos = data.getBall().getPosition();
                if(ballPos[0] >= -0.83f && ballPos[0] <= 0.83f && ballPos[2] >= 5.4f) {
                    // Gol para el equipo local
                    goGoalState(1);
                } else if(ballPos[0] >= -0.83f && ballPos[0] <= 0.83f &&  ballPos[2] <= -5.4f) {
                    // Gol para el equipo visitante
                    goGoalState(0);
                }
            }

            // Comprueba si los objetos han dejado de moverse
            if(moving != lastMoving && !moving) {

                // Si alguien ha tocado la pelota...
                if(ballPosessor != null) {

                    // Comprueba que el posesor es de tu equipo
                    boolean myPlayer = false;
                    for(Mesh cap : data.getCaps()) {
                        if(cap.getRigidBody() == ballPosessor) {
                            myPlayer = true;
                            break;
                        }
                    }
                    if(data.getKeeper().getRigidBody() == ballPosessor) {
                        myPlayer = true;
                    }

                    // Si no es de tu equipo, se acaba tu turno
                    if(!myPlayer) {
                        toggleTurn();
                    } else {
                        RigidBody ballRB = data.getBall().getRigidBody();
                        float x = ballPosessor.getPosition()[0] - ballRB.getPosition()[0];
                        float z = ballPosessor.getPosition()[2] - ballRB.getPosition()[2];
                        float dist = x * x + z * z;

                        // Si estamos en rango de pase, baja los chuts
                        if(dist < PASS_RANGE) {
                            if(data.getShoots() > 1) {
                                if(data.downShoots()) {     // El pase no cuenta como última jugada
                                    toggleTurn();
                                }
                            }
                        } else {
                            // No has pasado el balón bien, se acaba tu turno
                            toggleTurn();
                        }
                    }
                } else {
                    if(data.downShoots()) {
                        toggleTurn();
                    }
                }

                // Comienza la siguiente jugada, nadie ha tocado el balón
                ballPosessor = null;
            }

            lastMoving = moving;

            synchronized (this) {
                this.notify();
            }
        }
    };

    /**
     * @brief Constructor del ChoosingState
     * @param data      Datos de la partida
     * @param renderer  Renderizador actual
     */
    public ChoosingState(final GameData data, GLRendererImpl renderer) {

        data.getMainActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                data.getScoreText().setVisibility(View.VISIBLE);
                data.getTurnTimeText().setVisibility(View.VISIBLE);
                data.getMatchTimeText().setVisibility(View.VISIBLE);
                data.getShootsText().setVisibility(View.VISIBLE);
                data.getCenterText().setVisibility(View.INVISIBLE);
            }
        });

        this.renderer = renderer;
        this.arrow = data.getArrow();
        this.data = data;
        this.lastMoving = false;
        this.ballPosessor = null;
    }

    /**
     * @brief Cambia el turno
     */
    protected void toggleTurn() {
        data.getSoundPlayer().playWhistleSound();   // Avisa de un cambio de turno
        data.toggleTurn();
        renderer.changeGameState(new ChoosingState(data, renderer));
    }

    /**
     * @inheritDoc
     */
    public void onCollision(RigidBody b1, RigidBody b2) {

        if(b1 instanceof VerticalCylinderBody && b2 instanceof VerticalCylinderBody) {

            // Reproduce el sonido de choque
            data.getSoundPlayer().playReboundSound();

            // Actualiza el que tiene la pelota
            RigidBody ballRB = data.getBall().getRigidBody();
            if(b1 == ballRB) {
                ballPosessor = b2;
            } else if(b2 == ballRB) {
                ballPosessor = b1;
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUpdate(ShaderProgram shader) {

        // Dibuja la flecha, si es necesario
        if(selectedCap != null) {
            arrow.draw(shader, renderer.getCamera());
        }

        // Actualiza la GUI
        synchronized (updateRunnable) {
            data.getMainActivity().runOnUiThread(updateRunnable);
            try {
                updateRunnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @brief Selecciona un objeto
     * @param mesh          El objeto a seleccionar
     * @param currentRay    Rayo para hacer ray-test
     * @param x             Coordenada X del dedo
     * @param y             Coordenada Y del dedo
     * @return Si ha sido seleccionado
     */
    private boolean selectObject(Mesh mesh, float[] currentRay, float x, float y) {

        if(raySphereTest(mesh.getPosition(), ((VerticalCylinderBody)mesh.getRigidBody()).getRadius(), renderer.getCamera().getPosition(), currentRay)) {

            // Selecciona el elemento
            selectedCap = mesh;
            float[] pos = selectedCap.getPosition();
            arrow.setPosition(pos[0], 0.001f, pos[2]);
            arrow.setScale(0.0f, 0.0f, 0.0f);
            touchPointX = x;
            touchPointY = y;
            return true;
        }

        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDown(float x, float y) {

        if(selectedCap != null || data.getWorld().isMoving()) return;

        // Crea un rayo desde la cámara a donde hemos tocado
        float[] currentRay = calculateMouseRay(x, y);

        // Realiza un raytest para detectar el elemento tocado
        selectedCapID = 0;
        for(Mesh cap : data.getCaps()) {
            if(selectObject(cap, currentRay, x, y)) {
                return;
            }
            selectedCapID ++;
        }

        // Comprueba además el portero
        if(selectObject(data.getKeeper(), currentRay, x, y)) {
            selectedCapID = -1;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onMove(float x, float y) {

        // Debe haber una chapa seleccionada
        if(selectedCap == null) return;

        // Calcula el tamaño de la flecha
        Point screenSize = getScreenSize();
        float deltaX = (x - touchPointX) / (float) screenSize.x;
        float deltaY = (y - touchPointY) / (float) screenSize.y;
        float angle = (float) Math.toDegrees(Math.atan2(deltaY, -deltaX));
        float magnitude = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY) * ARROW_MAGNITUDE_MULT;
        if(magnitude > MAX_ARROW_MAGNITUDE) magnitude = MAX_ARROW_MAGNITUDE;

        // Establece el nuevo tamaño de la flecha
        arrow.setScale(magnitude, magnitude, magnitude);
        arrow.setRotation(angle, 0.0f, 1.0f, 0.0f);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUp(float x, float y) {

        // Debe haber una chapa seleccionada
        if(selectedCap == null) return;

        // Calcula el rayo
        float[] currentRay = calculateMouseRay(x, y);

        // Deselecciona la chapa actual
        if(raySphereTest(selectedCap.getPosition(), ((VerticalCylinderBody)selectedCap.getRigidBody()).getRadius(), renderer.getCamera().getPosition(), currentRay)) {
            selectedCap = null;
        } else {

            // Calcula el impulso
            float magnitude = arrow.getScale()[0] * IMPULSE_MULTIPLIER;
            float angle = (float) Math.toRadians(arrow.getRotation()[0]);
            float[] impulse = new float[3];
            impulse[0] = - magnitude * (float) Math.sin(angle);
            impulse[1] = 0.0f;
            impulse[2] = - magnitude * (float) Math.cos(angle);

            // Aplica el impulso a la chapa
            selectedCap.getRigidBody().applyImpulse(impulse);
            selectedCap = null;

            // Has lanzado una chapa
            data.getSoundPlayer().playKickSound();

            // Suma 2 segundos al reloj
            data.getMainActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    data.tickTurnTime(true);
                }
            });

            // Manda el tiro al otro jugador (online)
            sendShoot(selectedCapID, impulse[0], impulse[2]);
        }
    }

    /**
     * @brief Envía un movimiento al otro jugador (online)
     * @param capID     Chapa que ha realizado un movimiento
     * @param impulseX  Impulso en el eje X
     * @param impulseZ  Impulso en el eje Z
     */
    protected void sendShoot(int capID, float impulseX, float impulseZ) { }

    /**
     * @inheritDoc
     */
    @Override
    public void onScroll(float x, float y) {

        // No debe haber una chapa seleccionada
        if(selectedCap != null) return;

        // Mueve la cámara a donde digamos
        Point size = getScreenSize();
        float dx = -x / (float) size.x * SCROLL_SPEED_X;
        float dy = y / (float) size.y * SCROLL_SPEED_Y;
        renderer.getCamera().translate(dy, dx, -4.5f, 4.5f, -6.0f, 6.0f);
    }

    /**
     * @brief Comprueba la colisión entre una recta y una esfera
     * @param sphereCenter  Centro de la esfera
     * @param sphereRadius  Radio de la esfera
     * @param rayPoint      Origen del rayo
     * @param rayDirection  Dirección del rayo
     * @return
     */
    private boolean raySphereTest(float[] sphereCenter, float sphereRadius, float[] rayPoint, float[] rayDirection) {
        float[] sphereToRay = new float[3];
        sphereToRay[0] = sphereCenter[0] - rayPoint[0];
        sphereToRay[1] = sphereCenter[1] - rayPoint[1];
        sphereToRay[2] = sphereCenter[2] - rayPoint[2];

        float dot = sphereToRay[0] * rayDirection[0] + sphereToRay[1] * rayDirection[1] + sphereToRay[2] * rayDirection[2];
        float[] p = new float[3];
        p[0] = rayPoint[0] + dot * rayDirection[0];
        p[1] = rayPoint[1] + dot * rayDirection[1];
        p[2] = rayPoint[2] + dot * rayDirection[2];

        float[] sphereToP = new float[3];
        sphereToP[0] = sphereCenter[0] - p[0];
        sphereToP[1] = sphereCenter[1] - p[1];
        sphereToP[2] = sphereCenter[2] - p[2];
        float length = (float) Math.sqrt(sphereToP[0] * sphereToP[0] + sphereToP[1] * sphereToP[1] + sphereToP[2] * sphereToP[2]);

        return (length < sphereRadius);
    }

    /**
     * @brief Obtén el tamaño de la pantalla
     * @return  El tamaño en píxeles de la pantalla
     */
    private Point getScreenSize() {
        Display display = MainApplication.getInstance().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * @brief Calcula la dirección de un rayo
     * @param x     Posición X de la proyección 2D
     * @param y     Posición Y de la proyección 2D
     * @return  La dirección del rayo
     */
    private float[] calculateMouseRay(float x, float y) {

        MatrixSystem mtx = MainApplication.getInstance().getMatrixSystem();

        Point size = getScreenSize();

        float ndcX = (2.0f * x) / size.x - 1.0f;
        float ndcY = -((2.0f * y) / size.y - 1.0f);

        float[] clipCoords = new float[] {ndcX, ndcY, -1.0f, 1.0f};
        float[] invProjection = new float[16];
        float[] eyeCoords = new float[4];
        Matrix.invertM(invProjection, 0, mtx.getProjectionMatrix(), 0);
        Matrix.multiplyMV(eyeCoords, 0, invProjection, 0, clipCoords, 0);
        eyeCoords[2] = -1.0f;
        eyeCoords[3] = 0.0f;

        float[] invView = new float[16];
        float[] rayWorld = new float[4];
        Matrix.invertM(invView, 0, mtx.getViewMatrix(), 0);
        Matrix.multiplyMV(rayWorld, 0, invView, 0, eyeCoords, 0);

        float l = (float) Math.sqrt(rayWorld[0] * rayWorld[0] + rayWorld[1] * rayWorld[1] + rayWorld[2] * rayWorld[2]);
        rayWorld[0] /= l;
        rayWorld[1] /= l;
        rayWorld[2] /= l;

        return rayWorld;
    }
}
