package com.dam.chapas.opengl;

/**
 * @file Camera.java
 * @brief Manejo de la cámara
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.opengl.Matrix;

/**
 * @class Camera
 */
public class Camera {

    private float[] viewMatrix;
    private float[] position = new float[] {0.2f, 2.0f, 0.0f};
    private float[] eye = new float[] {0.0f, 0.0f, 0.0f};

    /**
     * @brief Constructor de una cámara
     */
    public Camera() {
        viewMatrix = new float[16];
    }

    /**
     * @brief Resetea la cámara
     */
    public void reset() {
        position[0] = 0.2f;
        position[1] = 2.0f;
        position[2] = 0.0f;
        eye[0] = 0.0f;
        eye[1] = 0.0f;
        eye[2] = 0.0f;
    }

    /**
     * @brief Establece la posición de la cámara
     * @param x Posición X
     * @param y Posición Y
     * @param z Posición Z
     */
    public void setPosition(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    /**
     * @brief Mueve la cámara dentro de unos límites
     * @param x     Posición X
     * @param z     Posición Z
     * @param minX  Posición X mínima
     * @param maxX  Posición X máxima
     * @param minZ  Posición Z mínima
     * @param maxZ  Posición Z máxima
     */
    public void translate(float x, float z, float minX, float maxX, float minZ, float maxZ) {

        float tmpX = position[0] + x;
        float tmpZ = position[2] + z;
        if(tmpX >= minX && tmpX <= maxX) {
            position[0] += x;
            eye[0] += x;
        }
        if(tmpZ >= minZ && tmpZ <= maxZ) {
            position[2] += z;
            eye[2] += z;
        }
    }

    /**
     * @brief Obtén la posición de la cámara
     * @return  La posición de la cámara
     */
    public float[] getPosition() {
        return this.position;
    }

    /**
     * @brief Establece el punto de mira de la cámara
     * @param x Mira en el eje X
     * @param y Mira en el eje Y
     * @param z Mira en el eje Z
     */
    public void setEye(float x, float y, float z) {
        eye[0] = x;
        eye[1] = y;
        eye[2] = z;
    }

    /**
     * @brief Actualiza la cámara en el shader
     * @param shader
     */
    public void enable(ShaderProgram shader) {
        shader.setUniform3fv("cameraPos", position);
    }

    /**
     * @brief Calcula la matriz de la cámara
     * @return
     */
    public float[] computeView() {
        Matrix.setLookAtM(viewMatrix, 0, position[0], position[1], position[2], eye[0], eye[1], eye[2], 0.0f, 1.0f, 0.0f);
        return viewMatrix;
    }
}
