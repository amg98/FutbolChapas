package com.dam.chapas.opengl;

/**
 * @file ShaderProgram.java
 * @brief Clase para la gestión de un shader
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.opengl.GLES20;
import android.util.Log;

import com.dam.chapas.app.MainApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @class ShaderProgram
 */
public class ShaderProgram {

    private int program;
    private HashMap<String, Integer> uniforms;

    /**
     * @brief Constructor de un shader
     * @param vertexShaderPath      Ruta del vertex shader
     * @param fragmentShaderPath    Ruta del fragment shader
     * @throws IOException          Si no se encuentra algún archivo
     * @throws RuntimeException     Si no se ha podido cargar el shader
     */
    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) throws IOException, RuntimeException {

        // Carga los shaders
        int vs = loadShader(GLES20.GL_VERTEX_SHADER, readFile(vertexShaderPath));
        int fs = loadShader(GLES20.GL_FRAGMENT_SHADER, readFile(fragmentShaderPath));

        // Crea el programa
        program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("glCreateProgram() failed. " + GLES20.glGetError());
        }

        // Asigna los shaders al programa
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);

        // Establece los atributos
        GLES20.glBindAttribLocation(program, 0, "vPosition");
        GLES20.glBindAttribLocation(program, 1, "vTexcoord");
        GLES20.glBindAttribLocation(program, 2, "vNormal");

        // Enlaza el programa
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[] {GLES20.GL_FALSE};
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            String log = GLES20.glGetProgramInfoLog(program);
            GLES20.glDeleteProgram(program);
            throw new RuntimeException(log);
        }

        // Borra los shaders
        GLES20.glDetachShader(program, vs);
        GLES20.glDetachShader(program, fs);
        GLES20.glDeleteShader(vs);
        GLES20.glDeleteShader(fs);

        // Registra los uniforms
        uniforms = new HashMap<String, Integer>();
        registerUniform("mvp");
        registerUniform("m");
        registerUniform("tex0");
        registerUniform("ambient");
        registerUniform("diffuse");
        registerUniform("specular");
        registerUniform("emissive");
        registerUniform("alpha");
        registerUniform("shininess");
        registerUniform("lightPos");
        registerUniform("lightColor");
        registerUniform("cameraPos");
    }

    /**
     * @brief Borra el shader
     */
    public void destroy() {
        GLES20.glUseProgram(0);
        GLES20.glDeleteProgram(program);
    }

    /**
     * @brief Registra un uniform
     * @param name  Nombre del uniform
     */
    public void registerUniform(String name) {
        uniforms.put(name, GLES20.glGetUniformLocation(program, name));
    }

    /**
     * @brief Carga un shader
     * @param type          Tipo de shader
     * @param shaderCode    Código del shader
     * @return Identificador del shader
     * @throws RuntimeException Si ocurre un error de compilación
     */
    private int loadShader(int type, String shaderCode) throws RuntimeException {

        // Crea el shader
        int shader = GLES20.glCreateShader(type);
        if(shader == 0) {
            throw new RuntimeException("Error creating shader");
        }

        // Compila el shader
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // Obtén el resultado de la compilación
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

        // Avisa si hay algún error
        if (compiled[0] == 0) {
            String log = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Could not compile shader:\n" + log);
        }

        // Devuelve el shader cargado
        return shader;
    }

    /**
     * @brief Activa el shader
     */
    public void enable() {
        GLES20.glUseProgram(program);
    }

    /**
     * @brief Establece un uniform como float
     * @param name  Nombre del uniform
     * @param data  Datos del uniform
     */
    public void setUniform1f(String name, float data) {
        GLES20.glUniform1f(uniforms.get(name), data);
    }

    /**
     * @brief Establece un uniform como 3-float
     * @param name  Nombre del uniform
     * @param data  Datos del uniform
     */
    public void setUniform3fv(String name, float[] data) {
        GLES20.glUniform3fv(uniforms.get(name), 1, data, 0);
    }

    /**
     * @brief Establece un uniform como 4-float
     * @param name  Nombre del uniform
     * @param data  Datos del uniform
     */
    public void setUniform4fv(String name, float[] data) {
        GLES20.glUniform4fv(uniforms.get(name), 1, data, 0);
    }

    /**
     * @brief Establece un uniform como una matriz 4x4
     * @param name  Nombre del uniform
     * @param data  Datos del uniform
     */
    public void setUniformMatrix4fv(String name, float[] data) {
        GLES20.glUniformMatrix4fv(uniforms.get(name), 1, false, data, 0);
    }

    /**
     * @brief Establece un uniform como int
     * @param name  Nombre del uniform
     * @param data  Datos del uniform
     */
    public void setUniform1i(String name, int data) {
        GLES20.glUniform1i(uniforms.get(name), data);
    }

    /**
     * @brief Lee un archivo de los assets
     * @param path  Ruta del archivo
     * @return El contenido del archivo
     * @throws IOException  Si no se encuentra el archivo
     */
    private String readFile(String path) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(MainApplication.getInstance().getAssets().open(path)));
        StringBuilder builder = new StringBuilder();

        String mLine;
        while ((mLine = reader.readLine()) != null) {
            builder.append(mLine).append('\n');
        }

        reader.close();

        return builder.toString();
    }
}
