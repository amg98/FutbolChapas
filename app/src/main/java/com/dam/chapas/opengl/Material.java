package com.dam.chapas.opengl;

/**
 * @file Material.java
 * @brief Clase modelo para un material
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.opengl.GLES20;

/**
 * @class Material
 */
public class Material {

    private float[] ambient = new float[] {1.0f, 1.0f, 1.0f};
    private float[] diffuse = new float[] {1.0f, 1.0f, 1.0f};
    private float[] specular = new float[] {0.5f, 0.5f, 0.5f};
    private float[] emissive = new float[] {0.0f, 0.0f, 0.0f};
    private float shininess;
    private float alpha;
    private Texture texture;

    /**
     * @brief Constructor del material
     */
    public Material() {
        setAlpha(1.0f);
        setTexture(null);
    }

    /**
     * @brief Clona un material
     * @return Un clon del material
     */
    public Material clone() {
        Material m = new Material();
        m.setAmbient(ambient[0], ambient[1], ambient[2]);
        m.setDiffuse(diffuse[0], diffuse[1], diffuse[2]);
        m.setSpecular(specular[0], specular[1], specular[2]);
        m.setEmissive(emissive[0], emissive[1], emissive[2]);
        m.setAlpha(alpha);
        m.setShininess(shininess);
        m.setTexture(texture);
        return m;
    }

    /**
     * @brief Actualiza el material en el shader
     * @param shader    Shader a actualizar
     */
    public void enable(ShaderProgram shader) {

        shader.enable();
        shader.setUniform3fv("ambient", ambient);
        shader.setUniform3fv("diffuse", diffuse);
        shader.setUniform3fv("specular", specular);
        shader.setUniform3fv("emissive", emissive);
        shader.setUniform1f("alpha", alpha);
        shader.setUniform1f("shininess", shininess);

        // La textura es opcional
        if(texture != null) {
            texture.enable(shader, GLES20.GL_TEXTURE0);
        }
    }

    /**
     * @brief Desactiva el material
     */
    public void disable() {
        Texture.disable(GLES20.GL_TEXTURE0);
    }

    /**
     * @brief Borra el material
     */
    public void delete() {
        if(texture != null) {
            texture.delete();
        }
    }

    /**
     * @brief Establece el brillo
     * @param shininess Brillo del material
     */
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    /**
     * @brief Obtén el brillo
     * @return  El brillo del material
     */
    public float getShininess() {
        return this.shininess;
    }

    /**
     * @brief Obtén el color ambiental
     * @return  El color ambiental
     */
    public float[] getAmbient() {
        return ambient;
    }

    /**
     * @brief Establece la componente ambiental
     * @param r Canal R
     * @param g Canal G
     * @param b Canal B
     */
    public void setAmbient(float r, float g, float b) {
        ambient[0] = r;
        ambient[1] = g;
        ambient[2] = b;
    }

    /**
     * @brief Obtén el color difuso
     * @return  El color difuso
     */
    public float[] getDiffuse() {
        return diffuse;
    }

    /**
     * @brief Establece la componente difusa
     * @param r Canal R
     * @param g Canal G
     * @param b Canal B
     */
    public void setDiffuse(float r, float g, float b) {
        diffuse[0] = r;
        diffuse[1] = g;
        diffuse[2] = b;
    }

    /**
     * @brief Obtén el color especular
     * @return  El color especular
     */
    public float[] getSpecular() {
        return specular;
    }

    /**
     * @brief Establece la componente especular
     * @param r Canal R
     * @param g Canal G
     * @param b Canal B
     */
    public void setSpecular(float r, float g, float b) {
        specular[0] = r;
        specular[1] = g;
        specular[2] = b;
    }

    /**
     * @brief Obtén el color emisivo
     * @return  El color emisivo
     */
    public float[] getEmissive() {
        return emissive;
    }

    public void setEmissive(float r, float g, float b) {
        emissive[0] = r;
        emissive[1] = g;
        emissive[2] = b;
    }

    /**
     * @brief Obtén la transparencia
     * @return  La transparencia
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * @brief Establece la transparencia del material
     * @param alpha La nueva transparencia
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * @brief Obtén la textura
     * @return  La textura del material
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * @brief Establece la textura del material
     * @param texture   La nueva textura
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
