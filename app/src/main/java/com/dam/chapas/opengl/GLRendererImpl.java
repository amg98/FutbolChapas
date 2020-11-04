package com.dam.chapas.opengl;

/**
 * @file GLRendererImpl.java
 * @brief Renderizador de gráficos OpenGL
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.bluetooth.BluetoothHelper;
import com.dam.chapas.game.ChoosingState;
import com.dam.chapas.game.GameData;
import com.dam.chapas.game.GameState;
import com.dam.chapas.game.InitState;
import com.dam.chapas.online.InitStateOnline;
import com.dam.chapas.online.WaitingStateOnline;
import com.dam.chapas.physics.World;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @class GLRendererImpl
 */
public class GLRendererImpl implements GLSurfaceView.Renderer {

    private ShaderProgram shader;
    private Camera cam;
    private Light sun;
    private GameState state;
    private GameData gameData;
    private World world;

    private final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                               float distanceX, float distanceY) {
            state.onScroll(distanceX, distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    });

    /**
     * @brief Cambia a un nuevo estado de la aplicación
     */
    public void changeGameState(GameState state) {
        BluetoothHelper btHelper = MainApplication.getInstance().getBluetoothHelper();
        if(!(state instanceof WaitingStateOnline) && btHelper != null) {
            btHelper.setRecvHandler(null);
        }
        this.state = state;
    }

    /**
     * @brief Llamado cuando se crea la superficie de dibujado
     */
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Inicializa OpenGL
        GLES20.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // Inicializa el sistema de texturas
        Texture.initialize();

        // Crea una luz direccional
        this.sun = new Light();
        sun.setPosition(0.0f, 10.0f, 0.0f);

        // Crea el simulador de físicas
        world = new World(new Runnable() {
            @Override
            public void run() {
                state.onCollision(world.getLastCollisionFirst(), world.getLastCollisionSecond());
            }
        });

        // Carga los datos del juego
        try {
            shader = new ShaderProgram("shader/shader.vs.glsl", "shader/shader.fs.glsl");
            gameData = new GameData(world);
        } catch(Exception e) {
            Log.e("CHAPAS", e.toString());
            e.printStackTrace();
        }

        // Crea la cámara
        this.cam = new Camera();

        // Establece el estado del juego
        if(MainApplication.getInstance().getBluetoothHelper() == null) {
            this.state = new InitState(gameData, this);
        } else {
            this.state = new InitStateOnline(gameData, this);
        }
    }

    /**
     * @brief Llamado cuando se dibuja un fotograma
     */
    public void onDrawFrame(GL10 unused) {

        // Limpia la pantalla
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Activa el shader
        shader.enable();
        sun.enable(shader);
        cam.enable(shader);

        // Obtén los modelos a dibujar
        Mesh[] keepers = gameData.getKeepers();
        Mesh stadium = gameData.getStadium();
        Mesh[][] caps = gameData.getAllCaps();
        Mesh ball = gameData.getBall();

        // Dibuja los porteros
        for(Mesh keeper : keepers) {
            keeper.draw(shader, cam);
        }

        // Dibuja las chapas
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < GameData.CAPS_PER_TEAM; j++) {
                caps[i][j].draw(shader, cam);
            }
        }

        // Dibuja la pelota
        ball.draw(shader, cam);

        // Dibuja el estadio
        stadium.draw(shader, cam);

        // Actualiza el estado del juego
        state.onUpdate(shader);

        // Actualiza la simulación de físicas
        world.update(1.0f / 60.0f);
    }

    /**
     * @brief Llamado cuando se redimensiona la superficie de dibujado
     * @param width     Nuevo ancho
     * @param height    Nuevo alto
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        MainApplication.getInstance().getMatrixSystem().onSurfaceChanged(width, height);
    }

    /**
     * @brief Llamado cuando se toca la pantalla
     * @param ev    Evento
     * @return  El resultado del evento
     */
    public boolean onTouchEvent(MotionEvent ev) {
        // No llamar a OpenGL aquí!
        float x = ev.getX();
        float y = ev.getY();
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                state.onDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                state.onMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                state.onUp(x, y);
                break;
        }
        return gestureDetector.onTouchEvent(ev);
    }

    /**
     * @brief Obtén la cámara
     * @return La cámara usada
     */
    public Camera getCamera() {
        return cam;
    }

    /**
     * @brief Libera los recursos
     */
    public void free() {
        gameData.free();
    }
}
