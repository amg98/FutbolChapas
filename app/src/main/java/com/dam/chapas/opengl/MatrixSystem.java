package com.dam.chapas.opengl;

/**
 * @file MatrixSystem.java
 * @brief Contenedor de matrices para dibujar elementos
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * @class MatrixSystem
 */
public class MatrixSystem {

    private float[] projectionMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] vpMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float[] translateMatrix = new float[16];
    private float[] rotateMatrix = new float[16];
    private float[] trMatrix = new float[16];
    private float[] scaleMatrix = new float[16];
    private float[] viewMatrix = null;

    /**
     * @brief Constructor de MatrixSystem
     */
    public MatrixSystem() {

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(vpMatrix, 0);
        Matrix.setIdentityM(mvpMatrix, 0);
    }

    /**
     * @brief Aplica una transformación elemental
     * @param pos   Posición
     * @param rot   Rotación (cuaternión)
     * @param scale Escala
     */
    public void setTransformation(float[] pos, float[] rot, float[] scale) {

        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, pos[0], pos[1], pos[2]);
        Matrix.setRotateM(rotateMatrix, 0, rot[0], rot[1], rot[2], rot[3]);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);

        Matrix.multiplyMM(trMatrix, 0, translateMatrix, 0, rotateMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, trMatrix, 0, scaleMatrix, 0);
    }

    /**
     * @brief Actualiza las matrices en el shader
     * @param shader    El shader a actualizar
     * @param camera    La cámara a usar
     */
    public void update(ShaderProgram shader, Camera camera) {
        viewMatrix = camera.computeView();
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
        shader.setUniformMatrix4fv("m", modelMatrix);
        shader.setUniformMatrix4fv("mvp", mvpMatrix);
    }

    /**
     * @brief Recalcula la matriz de proyección
     * @param width     Nuevo ancho de la pantalla
     * @param height    Nuevo alto de la pantalla
     */
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        //float fov = 45.0f * (float)Math.PI / 180.0f;
        float aspect = (float) width / (float) height;

        Matrix.frustumM(projectionMatrix, 0, -aspect, aspect, -1, 1, 1.0f, 1000.0f);
        //Matrix.perspectiveM(projectionMatrix, 0, fov, aspect, 1.0f, 1000.0f);
    }

    /**
     * @brief Obtén la matriz de proyección
     * @return  La matriz de proyección
     */
    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }

    /**
     * @brief Obtén la matriz de cámara
     * @return  La matriz de cámara
     */
    public float[] getViewMatrix() {
        return viewMatrix;
    }

    /**
     * @brief Obtén la matriz de modelado
     * @return  La matriz de modelado
     */
    public float[] getModelMatrix() {
        return modelMatrix;
    }
}
