package com.dam.chapas.physics;

/**
 * @file World.java
 * @brief Simulación de físicas
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @class World
 */
public class World {

    private List<RigidBody> rigidBodies;
    private boolean moving;
    private Runnable collisionCallback;
    private RigidBody b1, b2;

    /**
     * @brief Constructor del simulador de físicas
     */
    public World(Runnable collisionCallback) {

        rigidBodies = new ArrayList<>();
        moving = false;
        this.collisionCallback = collisionCallback;
        this.b1 = null;
        this.b2 = null;
    }

    /**
     * @brief Obtén el callback de las colisiones
     * @return Dicho callback
     */
    public Runnable getCollisionCallback() {
        return collisionCallback;
    }

    /**
     * @brief Establece la última colisión
     * @param b1    Primer cuerpo de la colisión
     * @param b2    Segundo cuerpo de la colisión
     */
    public void setLastCollision(RigidBody b1, RigidBody b2) {
        this.b1 = b1;
        this.b2 = b2;
    }

    /**
     * @brief Obtén el primer objeto de la última colisión
     * @return El primer objeto de la última colisión
     */
    public RigidBody getLastCollisionFirst() {
        return b1;
    }

    /**
     * @brief Obtén el segundo objeto de la última colisión
     * @return El segundo objeto de la última colisión
     */
    public RigidBody getLastCollisionSecond() {
        return b2;
    }

    /**
     * @brief Añade un nuevo cuerpo a la simulación
     * @param body  El cuerpo a añadir
     */
    public void addRigidBody(RigidBody body) {
        rigidBodies.add(body);
    }

    /**
     * @brief Actualiza la simulación
     * @param delta Paso de la simulación
     */
    public void update(float delta) {

        for(int i = 1; i < rigidBodies.size(); i++) {
            for(int j = 0; j < i; j++) {
                RigidBody a = rigidBodies.get(i);
                RigidBody b = rigidBodies.get(j);
                if(!a.checkHandleCollision(b, this)) {
                    b.checkHandleCollision(a, this);
                }
            }
        }

        moving = false;

        for(RigidBody body : rigidBodies) {
            if(body.update(delta)) {
                moving = true;
            }
        }
    }

    /**
     * @brief Obtén si hay algún cuerpo moviéndose
     * @return  Si hay algún cuerpo moviéndose
     */
    public boolean isMoving() {
        return moving;
    }
}
