package com.dam.chapas.opengl;

/**
 * @file Light.java
 * @brief Clase para luces direccionales
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

/**
 * @class Light
 */
public class Light {

    private float[] position = new float[] {0.0f, 0.0f, 0.0f};
    private float[] color = new float[] {1.0f, 1.0f, 1.0f};

    /**
     * @brief Constructor de una luz
     */
    public Light() { }

    /**
     * @brief Obtén la posición de la luz
     * @return  La posición de la luz
     */
    public float[] getPosition() {
        return position;
    }

    /**
     * @brief Establece la posición de la luz
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
     * @brief Obtén el color de la luz
     * @return  El color de la luz
     */
    public float[] getColor() {
        return color;
    }

    /**
     * @brief Establece el color de la luz
     * @param r Canal R
     * @param g Canal G
     * @param b Canal B
     */
    public void setColor(float r, float g, float b) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
    }

    /**
     * @brief Actualiza la luz en el shader
     * @param shader    Shader a actualizar
     */
    public void enable(ShaderProgram shader) {
        shader.setUniform3fv("lightPos", position);
        shader.setUniform3fv("lightColor", color);
    }
}
