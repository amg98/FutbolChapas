package com.dam.chapas.game;

/**
 * @file GameData.java
 * @brief Datos de una partida
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.dam.chapas.R;
import com.dam.chapas.app.MainApplication;
import android.util.Pair;

import com.dam.chapas.online.MovePdu;
import com.dam.chapas.opengl.GLRendererImpl;
import com.dam.chapas.opengl.Material;
import com.dam.chapas.opengl.Mesh;
import com.dam.chapas.opengl.ObjMesh;
import com.dam.chapas.physics.RigidBody;
import com.dam.chapas.physics.VerticalCylinderBody;
import com.dam.chapas.physics.WallBody;
import com.dam.chapas.physics.World;
import com.dam.chapas.sound.SoundPlayer;

import java.io.IOException;

/**
 * @class GameData
 */
public class GameData {

    enum MatchStatus {
        FIRST_TIME,
        SECOND_TIME,
        END,
    };

    private MatchStatus matchStatus;
    private Mesh ball;
    private Mesh[][] caps;
    private Mesh[] keepers;
    private Mesh stadium;
    private Mesh arrow;
    private int[] goals;
    private RigidBody[][] capsRB;
    private RigidBody ballRB;
    private RigidBody[] keepersRB;
    private boolean turn;
    private SoundPlayer soundPlayer;
    private int turnTime;
    private int totalTime;
    private int shoots;
    private World world;
    private static TextView scoreText;
    private static TextView turnTimeText;
    private static TextView matchTimeText;
    private static TextView shootsText;
    private static TextView centerText;
    private static Activity mainActivity;

    public static final int CAPS_PER_TEAM = 8;
    private static final float CAP_RADIUS = 1.17f;
    private static final float CAP_SCALE = 0.2f;
    private static final float WALL_THICK = 0.25f;
    private static final float BALL_SCALE = 0.17f;
    private static final float BALL_RADIUS = 0.85f;
    private static final float BALL_Y = 0.145f;
    private static final int TOTAL_TIME = 5 * 60 * 60;

    private final float[] capsPos = new float[] {
            2.0f, 0.0f, 4.25f,
            0.75f, 0.0f, 4.25f,
            -0.75f, 0.0f, 4.25f,
            -2.0f, 0.0f, 4.25f,
            1.5f, 0.0f, 2.5f,
            0.0f, 0.0f, 2.5f,
            -1.5f, 0.0f, 2.5f,
            0.0f, 0.0f, 1.0f,
            // ---------------
            2.0f, 0.0f, -4.25f,
            0.75f, 0.0f, -4.25f,
            -0.75f, 0.0f, -4.25f,
            -2.0f, 0.0f, -4.25f,
            1.5f, 0.0f, -2.5f,
            0.0f, 0.0f, -2.5f,
            -1.5f, 0.0f, -2.5f,
            0.0f, 0.0f, -1.0f,
    };

    /**
     * @brief Obtén los elementos del GUI
     */
    public static void setGuiElements(Activity mainActivity, TextView scoreText, TextView turnTimeText, TextView matchTimeText, TextView shootsText, TextView centerText) {
        GameData.scoreText = scoreText;
        GameData.turnTimeText = turnTimeText;
        GameData.matchTimeText = matchTimeText;
        GameData.shootsText = shootsText;
        GameData.centerText = centerText;
        GameData.mainActivity = mainActivity;
    }

    /**
     * @brief Actualiza los goles
     * @param team      Equipo que ha metido gol (0 = local, 1 = visitante)
     * @param renderer  Para resetear la escena
     */
    public void updateGoals(int team, GLRendererImpl renderer) {
        if(team == 0) {
            goals[0] ++;
        } else {
            goals[1] ++;
        }
        scoreText.setText(goals[0] + " - " + goals[1]);
        resetStatus(renderer, false);
    }

    /**
     * @brief Resetea el estado del juego (sin contar los goles ni el tiempo)
     * @param renderer  Para resetear la escena
     * @param resetTime Resetea el tiempo
     */
    public void resetStatus(GLRendererImpl renderer, boolean resetTime) {

        renderer.getCamera().reset();
        ball.setPosition(0.0f, BALL_Y, 0.0f);
        ballRB.setVelocity(0.0f, 0.0f, 0.0f);
        keepers[0].setPosition(0.0f, 0.001f, 5.0f);
        keepersRB[0].setVelocity(0.0f, 0.0f, 0.0f);
        keepers[1].setPosition(0.0f, 0.001f, -5.0f);
        keepersRB[1].setVelocity(0.0f, 0.0f, 0.0f);
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < CAPS_PER_TEAM; j++) {
                int index = i * CAPS_PER_TEAM + j;
                caps[i][j].setPosition(capsPos[index * 3], capsPos[index * 3 + 1], capsPos[index * 3 + 2]);
                capsRB[i][j].setVelocity(0.0f, 0.0f, 0.0f);
            }
        }

        if(resetTime) {
            totalTime = TOTAL_TIME;
            int t = totalTime / 60;
            matchTimeText.setText(String.format("%02d:%02d", t / 60, t % 60));
        }
    }

    /**
     * @brief Actualiza el tiempo del turno
     * @param action    Si ha realizado una acción
     * @return Si se ha acabado el tiempo
     */
    public boolean tickTurnTime(boolean action) {
        if(action) {
            turnTime += 2 * 60;
        } else if (turnTime > 0){
            turnTime --;
        }
        turnTimeText.setText(String.format("%02d", turnTime / 60));
        return (turnTime == 0);
    }

    /**
     * @brief Establece el tiempo de turno
     * @param turnTime  El nuevo tiempo de turno
     */
    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    /**
     * @brief Obtén el tiempo de turno
     * @return  El tiempo de turno
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * @brief Baja el tiempo de turno
     * @return  Si ha llegado a 0
     */
    public boolean tickTime() {
        if(totalTime > 0) totalTime --;
        int t = totalTime / 60;
        matchTimeText.setText(String.format("%02d:%02d", t / 60, t % 60));
        return (totalTime == 0);
    }

    /**
     * @brief Establece el tiempo total de una parte
     * @param totalTime El tiempo total
     */
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * @brief Actualiza el número de chuts
     * @return Si se han acabado los chuts
     */
    public boolean downShoots() {
        shoots --;
        shootsText.setText(shoots + " chuts");
        if(shoots > 0 && MainApplication.getInstance().getBluetoothHelper() != null) {
            MovePdu pdu = new MovePdu();
            pdu.setShoots(shoots);
            pdu.send(MainApplication.getInstance().getBluetoothHelper().getBluetoothService());
        }
        return (shoots == 0);
    }

    /**
     * @brief Establece los chuts restantes
     * @param shoots    Los chuts restantes
     */
    public void setShoots(int shoots) {
        this.shoots = shoots;
        shootsText.setText(shoots + " chuts");
    }

    /**
     * @brief Obtén el número de chutes restantes
     * @return  Los chutes restantes
     */
    public int getShoots() {
        return shoots;
    }

    /**
     * @brief Obtén el texto del marcador
     * @return  El texto del marcador
     */
    public TextView getScoreText() {
        return scoreText;
    }

    /**
     * @brief Obtén el texto del tiempo de turno
     * @return  El texto del tiempo de turno
     */
    public TextView getTurnTimeText() {
        return turnTimeText;
    }

    /**
     * @brief Obtén el texto del tiempo restante
     * @return  El texto del tiempo restante
     */
    public TextView getMatchTimeText() {
        return matchTimeText;
    }

    /**
     * @brief Obtén el texto de los chutes restantes
     * @return  El texto de los chutes restantes
     */
    public TextView getShootsText() {
        return shootsText;
    }

    /**
     * @brief Obtén el texto central
     * @return  El texto central
     */
    public TextView getCenterText() {
        return centerText;
    }

    /**
     * @brief Obtén la actividad principal
     * @return  La actividad principal
     */
    public Activity getMainActivity() {
        return mainActivity;
    }

    /**
     * @brief Obtén el reproductor de audio
     * @return El reproductor de audio
     */
    public SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }

    /**
     * @brief Obtén el simulador de físicas
     * @return El simulador de físicas
     */
    public World getWorld() {
        return world;
    }

    /**
     * @brief Obtén el estado del partido
     * @return  El estado del partido
     */
    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    /**
     * @brief Establece el estado del partido
     * @param status    Nuevo estado del partido
     */
    public void setMatchStatus(MatchStatus status) {
        matchStatus = status;
    }

    /**
     * @brief Obtén el tiempo restante de una parte
     * @return  El tiempo restante de una parte
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * @brief Constructor de los datos de la partida
     * @param world     El mundo para la simulación de físicas
     * @throws IOException  Si no se ha podido cargar algún elemento
     */
    public GameData(World world) throws IOException {

        // Inicializa variables
        goals = new int[] {0, 0};
        totalTime = TOTAL_TIME;
        this.world = world;
        matchStatus = MatchStatus.FIRST_TIME;

        // Inicializa la GUI
        mainActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                setTurn(false);
                scoreText.setText(goals[0] + " - " + goals[1]);
                int t = totalTime / 60;
                matchTimeText.setText(String.format("%02d:%02d", t / 60, t % 60));
            }
        });

        // Ve cargando los sonidos
        soundPlayer = new SoundPlayer(mainActivity);

        // Carga la pelota
        ball = new ObjMesh("model/ball.obj");
        ball.setPosition(0.0f, BALL_Y, 0.0f);
        ball.setScale(BALL_SCALE, BALL_SCALE, BALL_SCALE);
        ballRB = new VerticalCylinderBody(BALL_RADIUS);
        ballRB.setMass(1.0f);
        ballRB.setRotationSpeed(1.5f);
        ballRB.linkToMesh(ball);
        world.addRigidBody(ballRB);

        // Carga las chapas
        caps = new Mesh[2][CAPS_PER_TEAM];
        capsRB = new RigidBody[2][CAPS_PER_TEAM];
        caps[0][0] = new ObjMesh("model/chapa.obj");
        caps[0][0].setScale(CAP_SCALE, CAP_SCALE, CAP_SCALE);
        caps[0][0].setPosition(capsPos[0], capsPos[1], capsPos[2]);
        capsRB[0][0] = new VerticalCylinderBody(CAP_RADIUS);
        capsRB[0][0].linkToMesh(caps[0][0]);
        capsRB[0][0].setMass(3.0f);
        int matStart = caps[0][0].getMaterialGroups().get(0).first;
        Material mat = caps[0][0].getMaterialGroups().get(0).second.clone();
        mat.setDiffuse(0.02f, 0.64f, 0.02f);
        world.addRigidBody(capsRB[0][0]);
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < CAPS_PER_TEAM; j++) {
                if(i == 0 && j == 0) continue;
                int index = i * CAPS_PER_TEAM + j;
                caps[i][j] = caps[0][0].clone();
                caps[i][j].setScale(CAP_SCALE, CAP_SCALE, CAP_SCALE);
                caps[i][j].setPosition(capsPos[index * 3], capsPos[index * 3 + 1], capsPos[index * 3 + 2]);
                capsRB[i][j] = new VerticalCylinderBody(CAP_RADIUS);
                capsRB[i][j].setMass(3.0f);
                capsRB[i][j].linkToMesh(caps[i][j]);
                world.addRigidBody(capsRB[i][j]);

                if(i == 1){
                    caps[i][j].getMaterialGroups().set(0, new Pair<>(matStart, mat));
                }
            }
        }

        // Carga los porteros
        keepers = new Mesh[2];
        keepersRB = new RigidBody[2];

        keepers[0] = new ObjMesh("model/tapon.obj");
        keepers[0].setScale(CAP_SCALE, CAP_SCALE, CAP_SCALE);
        keepers[0].setPosition(0.0f, 0.001f, 5.0f);
        keepersRB[0] = new VerticalCylinderBody(1.0f);
        keepersRB[0].setMass(3.0f);
        keepersRB[0].linkToMesh(keepers[0]);
        world.addRigidBody(keepersRB[0]);

        keepers[1] = keepers[0].clone();
        keepers[1].setScale(CAP_SCALE, CAP_SCALE, CAP_SCALE);
        keepers[1].setPosition(0.0f, 0.001f, -5.0f);
        keepersRB[1] = new VerticalCylinderBody(1.0f);
        keepersRB[1].setMass(3.0f);
        keepersRB[1].linkToMesh(keepers[1]);
        world.addRigidBody(keepersRB[1]);

        // Carga la flecha
        arrow = new ObjMesh("model/flecha.obj");
        arrow.setPosition(0.0f, 0.001f, 0.0f);
        arrow.setScale(0.2f, 0.2f, 0.2f);

        // Carga el estadio
        stadium = new ObjMesh("model/stadium.obj");

        // Crea las paredes superior e inferior
        world.addRigidBody(new WallBody(-4.0f + CAP_RADIUS/2.0f * CAP_SCALE, -10.0f,
                                        -4.0f + CAP_RADIUS/2.0f * CAP_SCALE, 10.0f, WALL_THICK));
        world.addRigidBody(new WallBody(4.0f - CAP_RADIUS/2.0f * CAP_SCALE, -10.0f,
                                        4.0f - CAP_RADIUS/2.0f * CAP_SCALE, 10.0f, WALL_THICK));

        // Crea la pared izquierda
        world.addRigidBody(new WallBody(-10.0f, 6.0f - CAP_RADIUS/2.0f * CAP_SCALE,
                                        -1.0f, 6.0f - CAP_RADIUS/2.0f * CAP_SCALE, WALL_THICK));
        world.addRigidBody(new WallBody(1.0f, 6.0f - CAP_RADIUS/2.0f * CAP_SCALE,
                                        10.0f, 6.0f - CAP_RADIUS/2.0f * CAP_SCALE, WALL_THICK));

        // Crea la pared derecha
        world.addRigidBody(new WallBody(-10.0f, -6.0f + CAP_RADIUS/2.0f * CAP_SCALE,
                                        -1.0f, -6.0f + CAP_RADIUS/2.0f * CAP_SCALE, WALL_THICK));
        world.addRigidBody(new WallBody(1.0f, -6.0f + CAP_RADIUS/2.0f * CAP_SCALE,
                                        10.0f, -6.0f + CAP_RADIUS/2.0f * CAP_SCALE, WALL_THICK));

        // Crea la pared de gol de cada portería
        world.addRigidBody(new WallBody(-1.0f, 6.4f - CAP_RADIUS/2.0f * CAP_SCALE,
                                        1.0f, 6.4f - CAP_RADIUS/2.0f * CAP_SCALE, WALL_THICK));
        world.addRigidBody(new WallBody(-1.0f, -6.4f + CAP_RADIUS/2.0f * CAP_SCALE,
                                        1.0f, -6.4f + CAP_RADIUS/2.0f * CAP_SCALE, WALL_THICK));

        // Crea el lado superior de las porterías
        world.addRigidBody(new WallBody(-1.0f - CAP_RADIUS/2.0f * CAP_SCALE, 6.4f,
                                        -1.0f - CAP_RADIUS/2.0f * CAP_SCALE, 6.0f, WALL_THICK));
        world.addRigidBody(new WallBody(-1.0f - CAP_RADIUS/2.0f * CAP_SCALE, -6.4f,
                                        -1.0f - CAP_RADIUS/2.0f * CAP_SCALE, -6.0f, WALL_THICK));

        // Crea el lado inferior de las porterías
        world.addRigidBody(new WallBody(1.0f + CAP_RADIUS/2.0f * CAP_SCALE, 6.4f,
                                        1.0f + CAP_RADIUS/2.0f * CAP_SCALE, 6.0f, WALL_THICK));
        world.addRigidBody(new WallBody(1.0f + CAP_RADIUS/2.0f * CAP_SCALE, -6.4f,
                                        1.0f + CAP_RADIUS/2.0f * CAP_SCALE, -6.0f, WALL_THICK));
    }

    /**
     * @brief Libera los recursos
     */
    public void free() {
        soundPlayer.free();
        ball.delete();
        stadium.delete();
        keepers[0].delete();
        keepers[1].delete();
        arrow.delete();
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < CAPS_PER_TEAM; j++) {
                caps[i][j].delete();
            }
        }
    }

    /**
     * @brief Cambia el turno
     */
    public void toggleTurn() {
        setTurn(!turn);
    }

    /**
     * @brief Establece el turno
     * @param turn  De quién es el turno ahora
     */
    public void setTurn(boolean turn) {
        this.turn = turn;

        turnTime = 20 * 60;
        shoots = 3;

        turnTimeText.setText(String.format("%02d", turnTime / 60));
        shootsText.setText(shoots + " chuts");
    }

    /**
     * @brief Obtén los porteros
     * @return Los porteros de ambos equipos
     */
    public Mesh[] getKeepers() {
        return keepers;
    }

    /**
     * @brief Obtén el portero
     * @return  El portero del equipo actual
     */
    public Mesh getKeeper() {
        return keepers[turn ? 1 : 0];
    }

    /**
     * @brief Obtén las chapas
     * @return Las chapas del equipo actual
     */
    public Mesh[] getCaps() {
        return caps[turn ? 1 : 0];
    }

    /**
     * @brief Obtén las chapas de ambos equipos
     * @return Las chapas de ambos equipos
     */
    public Mesh[][] getAllCaps() {
        return caps;
    }

    /**
     * @brief Obtén la pelota
     * @return  La pelota
     */
    public Mesh getBall() {
        return ball;
    }

    /**
     * @brief Obtén el estadio
     * @return El estadio
     */
    public Mesh getStadium() {
        return stadium;
    }

    /**
     * @brief Obtén la flecha
     * @return La flecha
     */
    public Mesh getArrow() {
        return arrow;
    }
}
