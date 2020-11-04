package com.dam.chapas.opengl;

/**
 * @file VBO.java
 * @brief Clase para un VBO (Vertex Buffer Object)
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @class VBO
 */
public class VBO {

    private int ncomponents;
    private int[] bufferID;

    public static final int VERTICES = 3;
    public static final int TEXCOORDS = 2;
    public static final int NORMALS = 3;

    /**
     * @brief Constructor de un VBO
     * @param buffer        Datos del VBO
     * @param ncomponents   Número de componentes
     */
    public VBO(float[] buffer, int ncomponents) {

        ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length * Float.BYTES);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = bb.asFloatBuffer();
        floatBuffer.put(buffer);
        floatBuffer.position(0);

        this.ncomponents = ncomponents;

        bufferID = new int[1];
        GLES20.glGenBuffers(1, bufferID, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferID[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.length * Float.BYTES, floatBuffer, GLES20.GL_STATIC_DRAW);
    }

    /**
     * @brief Comienza el dibujado del VBO
     * @param attribute ID del atributo en el shader
     */
    public void beginDraw(int attribute) {
        GLES20.glEnableVertexAttribArray(attribute);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferID[0]);
        GLES20.glVertexAttribPointer(attribute, this.ncomponents, GLES20.GL_FLOAT, false, 0, 0);
    }

    /**
     * @brief Finaliza el dibujado del VBO
     * @param attribute ID del atributo en el shader
     */
    public void endDraw(int attribute) {
        GLES20.glDisableVertexAttribArray(attribute);
    }

    /**
     * @brief Borra el VBO
     */
    public void delete() {
        GLES20.glDeleteBuffers(1, bufferID, 0);
    }
}
