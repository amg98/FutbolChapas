package com.dam.chapas.opengl;

/**
 * @file GLSurfaceViewImpl.java
 * @brief Superficie de dibujado OpenGL
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dam.chapas.game.GameState;

/**
 * @class GLSurfaceViewImpl
 */
public class GLSurfaceViewImpl extends GLSurfaceView {

    private final GLRendererImpl renderer;

    /**
     * @brief Constructor de GLSurfaceViewImpl
     * @param context
     * @param attr
     */
    public GLSurfaceViewImpl(Context context, AttributeSet attr){
        super(context, attr);

        setEGLContextClientVersion(2);

        renderer = new GLRendererImpl();

        setRenderer(renderer);
    }

    /**
     * @brief Obtén el renderer
     * @return  El renderer
     */
    public final GLRendererImpl getRenderer() {
        return renderer;
    }

    /**
     * @brief Llamado cuando tocamos la pantalla
     * @param ev    Evento
     * @return  El resultado del evento
     */
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        return renderer.onTouchEvent(ev);
    }
}
