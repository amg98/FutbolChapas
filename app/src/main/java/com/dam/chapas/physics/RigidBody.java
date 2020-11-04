package com.dam.chapas.physics;

/**
 * @file RigidBody.java
 * @brief Clase para un cuerpo rígido
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.opengl.Mesh;

/**
 * @class RigidBody
 */
public abstract class RigidBody {

    protected float mass;
    protected float friction;
    protected float[] velocity = new float[] {0.0f, 0.0f, 0.0f};
    protected float[] position = new float[] {0.0f, 0.0f, 0.0f};
    protected float[] rotation = new float[] {0.0f, 1.0f, 0.0f, 0.0f};
    protected float[] scale = new float[] {1.0f, 1.0f, 1.0f};
    protected float rotationSpeed;

    private static final float VELOCITY_EPSILON = 0.01f;

    /**
     * @brief Constructor de un RigidBody
     */
    protected RigidBody() {

        this.mass = 1.0f;
        this.friction = 0.8f;
        this.rotationSpeed = 0.0f;
    }

    /**
     * @brief Establece la velocidad de rotación
     * @param rotationSpeed La velocidad de rotación
     */
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    /**
     * @brief Establece la masa del cuerpo
     * @param mass  La masa del cuerpo
     */
    public void setMass(float mass) {
        this.mass = mass;
    }

    /**
     * @brief Obtén la masa del cuerpo
     * @return  La masa del cuerpo
     */
    public float getMass() {
        return this.mass;
    }

    /**
     * @brief Establece el coeficiente de rozamiento del cuerpo
     * @param friction  El coeficiente de rozamiento del cuerpo
     */
    public void setFriction(float friction) {
        this.friction = friction;
    }

    /**
     * @brief Obtén el coeficiente de rozamiento del cuerpo
     * @return  El coeficiente de rozamiento del cuerpo
     */
    public float getFriction() { return this.friction; }

    /**
     * @brief Enlaza un Mesh a este cuerpo
     * @param mesh  El mesh asociado a este cuerpo
     */
    public void linkToMesh(Mesh mesh) {
        mesh.setRigidBody(this);
        position = mesh.getPosition();
        rotation = mesh.getRotation();
        scale = mesh.getScale();
    }

    /**
     * @brief Establece la posición de este cuerpo
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
     * @brief Obtén la posición de este cuerpo
     * @return  La posición de este cuerpo
     */
    public float[] getPosition() {
        return position;
    }

    /**
     * @brief Establece la velocidad de este cuerpo
     * @param vx    Velocidad X
     * @param vy    Velocidad Y
     * @param vz    Velocidad Z
     */
    public void setVelocity(float vx, float vy, float vz) {
        velocity[0] = vx;
        velocity[1] = vy;
        velocity[2] = vz;
    }

    /**
     * @brief Obtén la velocidad de este cuerpo
     * @return  La velocidad de este cuerpo
     */
    public float[] getVelocity() {
        return velocity;
    }

    /**
     * @brief Aplica un impulso a este cuerpo
     * @param impulse   Vector del impulso
     */
    public void applyImpulse(float[] impulse) {
        velocity[0] += impulse[0] / mass;
        velocity[1] += impulse[1] / mass;
        velocity[2] += impulse[2] / mass;
    }

    /**
     * @brief Actualiza la simulación cinemática de este cuerpo
     * @param delta Incremento de tiempo
     * @return  Si el objeto ha cambiado de posición
     */
    public boolean update(float delta) {

        // Actualiza la posición
        position[0] += velocity[0] * delta;
        position[1] += velocity[1] * delta;
        position[2] += velocity[2] * delta;

        // Actualiza la velocidad
        velocity[0] -= friction * velocity[0] * delta;
        velocity[1] -= friction * velocity[1] * delta;
        velocity[2] -= friction * velocity[2] * delta;

        float velMagnitude = velocity[0] * velocity[0] + velocity[1] * velocity[1] + velocity[2] * velocity[2];
        if(velMagnitude > VELOCITY_EPSILON) {

            // Actualiza la rotación en la dirección de la velocidad
            velMagnitude = (float) Math.sqrt(velMagnitude);
            rotation[0] += rotationSpeed;
            rotation[1] = velocity[2] / velMagnitude;
            rotation[2] = 0.0f;
            rotation[3] = -velocity[0] / velMagnitude;
        } else {

            // El objeto está quieto
            velocity[0] = 0.0f;
            velocity[1] = 0.0f;
            velocity[2] = 0.0f;
            return false;
        }

        // El objeto se mueve
        return true;
    }

    /**
     * @brief Comprueba y maneja una colisión (estática y dinámica)
     * @param body  Cuerpo con el que comprobar la colisión
     * @param world Entorno de simulación
     * @return  Si se soporta la colisión con este cuerpo
     */
    public abstract boolean checkHandleCollision(RigidBody body, World world);
}
