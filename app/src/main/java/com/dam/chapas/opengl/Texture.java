package com.dam.chapas.opengl;

/**
 * @file Texture.java
 * @brief Clase de gestión de texturas
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.dam.chapas.app.MainApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @class Texture
 */
public class Texture {

    private static int[] emptyTexture;

    private int[] id;

    /**
     * @brief Inicializa el gestor de texturas
     */
    public static void initialize() {

        ByteBuffer bb = ByteBuffer.allocateDirect(4);
        bb.put(new byte[] {-128, -128, -128, -128});
        bb.position(0);

        // Crea una textura 1x1 vacía (para los colores planos)
        emptyTexture = new int[1];
        GLES20.glGenTextures(1, emptyTexture, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, emptyTexture[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 1, 1, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bb);
    }

    /**
     * @brief Finaliza el gestor de texturas
     */
    public static void finish() {
        GLES20.glDeleteTextures(1, emptyTexture, 0);
    }

    /**
     * @brief Carga una textura
     * @param path  Ruta de la textira
     * @throws RuntimeException Si ocurre algún error durante la carga
     * @throws IOException      Si no se encuentra la textura
     */
    public Texture(String path) throws RuntimeException, IOException {

        this.id = new int[1];

        // Genera la textura
        GLES20.glGenTextures(1, id, 0);

        if (id[0] == 0) {
            throw new RuntimeException("Couldn't create texture");
        }

        // Selecciona la textura
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id[0]);

        // Establece parámetros de filtrado y repetición
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Carga la textura (puede ser comprimida o no)
        if(path.endsWith("pkm")) {
            loadETC1(path);
        } else {
            loadImage(path);
        }
    }

    /**
     * @brief Borra la textura
     */
    public void delete() {
        GLES20.glDeleteTextures(1, this.id, 0);
    }

    /**
     * @brief Activa la textura
     * @param shader    Shader donde activarla
     * @param unit      Slot de la textura
     */
    public void enable(ShaderProgram shader, int unit) {
        GLES20.glActiveTexture(unit);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id[0]);
        shader.setUniform1i("tex" + (unit - GLES20.GL_TEXTURE0), 0);
    }

    /**
     * @brief Desactiva la textura
     * @param unit  Slot de la textura
     */
    public static void disable(int unit) {
        GLES20.glActiveTexture(unit);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, emptyTexture[0]);
    }

    /**
     * @brief Carga una textura comprimida en formato ETC1
     * @param path          Ruta del archivo
     * @throws IOException  Si no se encuentra el archivo
     */
    private void loadETC1(String path) throws IOException {
        InputStream is = MainApplication.getInstance().getAssets().open(path);
        ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, is);
        is.close();
    }

    /**
     * @brief Carga una textura a partir de una imagen
     * @param path          Ruta del archivo
     * @throws IOException  Si no se encuentra el archivo
     */
    private void loadImage(String path) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // Sin pre-scaling
        InputStream is = MainApplication.getInstance().getAssets().open(path);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        is.close();
    }
}
