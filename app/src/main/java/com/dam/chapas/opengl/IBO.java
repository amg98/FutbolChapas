package com.dam.chapas.opengl;

/**
 * @file IBO.java
 * @brief Clase para los Index Buffer Objects (IBO)
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * @class IBO
 */
public class IBO {

    private int[] bufferID;

    /**
     * @brief Constructor de un IBO
     * @param buffer    Buffer con los índices
     */
    public IBO(short[] buffer) {

        ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length * Short.BYTES);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer shortBuffer = bb.asShortBuffer();
        shortBuffer.put(buffer);
        shortBuffer.position(0);

        bufferID = new int[1];
        GLES20.glGenBuffers(1, bufferID, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferID[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer.length * Short.BYTES, shortBuffer, GLES20.GL_STATIC_DRAW);
    }

    /**
     * @brief Dibuja el IBO
     * @param vboArray  VBOs en los que se apoya
     * @param start     Índice inicial
     * @param end       Índice final
     */
    public void draw(VBO[] vboArray, int start, int end) {

        for(int i = 0; i < vboArray.length; i++) {
            vboArray[i].beginDraw(i);
        }

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferID[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, end - start, GLES20.GL_UNSIGNED_SHORT, start * Short.BYTES);

        for(int i = 0; i < vboArray.length; i++) {
            vboArray[i].endDraw(i);
        }
    }

    /**
     * @brief Borra el IBO
     */
    public void delete() {
        GLES20.glDeleteBuffers(1, bufferID, 0);
    }
}
