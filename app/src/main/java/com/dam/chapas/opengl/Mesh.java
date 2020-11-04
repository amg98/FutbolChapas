package com.dam.chapas.opengl;

/**
 * @file Mesh.java
 * @brief Clase para almacenar un modelo 3D
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.util.Pair;

import com.dam.chapas.app.MainApplication;
import com.dam.chapas.physics.RigidBody;

import java.util.ArrayList;

/**
 * @class Mesh
 */
public class Mesh {

    protected VBO[] vbos;
    protected IBO ibo;
    protected ArrayList<Pair<Integer, Material>> materialGroups;
    protected Material[] materials;
    private float[] pos;
    private float[] rot;
    private float[] scale;
    private boolean cloned;
    private RigidBody rigidBody;

    /**
     * @brief Constructor de un Mesh
     */
    protected Mesh() {
        vbos = null;
        ibo = null;
        materials = null;
        materialGroups = null;
        pos = new float[] {0.0f, 0.0f, 0.0f};
        rot = new float[] {0.0f, 1.0f, 0.0f, 0.0f};
        scale = new float[] {1.0f, 1.0f, 1.0f};
        cloned = false;
        rigidBody = null;
    }

    /**
     * @brief Constructor de un Mesh
     * @param vbos              Lista de VBOs
     * @param ibo               IBO
     * @param materials         Materiales a usar
     * @param materialGroups    Grupos de materiales
     */
    private Mesh(VBO[] vbos, IBO ibo, Material[] materials, ArrayList<Pair<Integer, Material>> materialGroups) {
        this.vbos = vbos;
        this.ibo = ibo;
        this.materials = materials;
        this.materialGroups = new ArrayList<>();
        for(Pair<Integer, Material> m : materialGroups) {
            this.materialGroups.add(new Pair<>(m.first, m.second));
        }
        pos = new float[] {0.0f, 0.0f, 0.0f};
        rot = new float[] {0.0f, 1.0f, 0.0f, 0.0f};
        scale = new float[] {1.0f, 1.0f, 1.0f};
        cloned = true;
    }

    /**
     * @brief Dibuja un modelo 3D en pantalla
     * @param shader    Shader a usar
     * @param cam       Cámara a usar
     */
    public void draw(ShaderProgram shader, Camera cam) {

        // Activa el shader
        shader.enable();

        // Realiza las transformaciones
        MatrixSystem mtx = MainApplication.getInstance().getMatrixSystem();
        mtx.setTransformation(pos, rot, scale);
        mtx.update(shader, cam);

        // Dibuja el modelo 3D
        int start = 0;
        for(Pair<Integer, Material> matGroup : materialGroups) {
            matGroup.second.enable(shader);
            ibo.draw(vbos, start, matGroup.first);
            matGroup.second.disable();
            start = matGroup.first;
        }
    }

    /**
     * @brief Borra el modelo 3D
     */
    public void delete() {
        if(cloned) return;
        for(Material mat : materials) {
            mat.delete();
        }
        for(VBO vbo : vbos) {
            vbo.delete();
        }
        ibo.delete();
    }

    /**
     * @brief Establece la posición del Mesh
     * @param x Posición X
     * @param y Posición Y
     * @param z Posición Z
     */
    public void setPosition(float x, float y, float z) {
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
    }

    /**
     * @brief Obtén la posición del Mesh
     * @return  La posición del Mesh
     */
    public float[] getPosition() {
        return this.pos;
    }

    /**
     * @brief Obtén el cuaternión del Mesh
     * @return  La rotación del Mesh
     */
    public float[] getRotation() {
        return this.rot;
    }

    /**
     * @brief Obtén la escala del Mesh
     * @return  La escala del Mesh
     */
    public float[] getScale() {
        return this.scale;
    }

    /**
     * @brief Establece la rotación del Mesh
     * @param a Ángulo, en radianes
     * @param x Eje X
     * @param y Eje Y
     * @param z Eje Z
     */
    public void setRotation(float a, float x, float y, float z) {
        rot[0] = a;
        rot[1] = x;
        rot[2] = y;
        rot[3] = z;
    }

    /**
     * @brief Establece la escala del Mesh
     * @param x Escala X
     * @param y Escala Y
     * @param z Escala Z
     */
    public void setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
    }

    /**
     * @brief Traslada el Mesh
     * @param x Traslación en el eje X
     * @param y Traslación en el eje Y
     * @param z Traslación en el eje Z
     */
    public void translate(float x, float y, float z) {
        pos[0] += x;
        pos[1] += y;
        pos[2] += z;
    }

    /**
     * @brief Establece el cuerpo rígido del Mesh
     * @param rb    El cuerpo rígido del Mesh
     */
    public void setRigidBody(RigidBody rb) {
        this.rigidBody = rb;
    }

    /**
     * @brief Obtén el cuerpo rígido del Mesh
     * @return  El cuerpo rígido del Mesh
     */
    public RigidBody getRigidBody() {
        return this.rigidBody;
    }

    /**
     * @brief Clona un Mesh
     * @return  El clon del Mesh
     */
    public Mesh clone() {
        return new Mesh(vbos, ibo, materials, materialGroups);
    }

    /**
     * @brief Obtén los grupos de materiales
     * @return Los grupos de materiales del Mesh
     */
    public ArrayList<Pair<Integer, Material>> getMaterialGroups() {
        return materialGroups;
    }
}
