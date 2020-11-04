package com.dam.chapas.physics;

/**
 * @file VerticalCylinderBody.java
 * @brief Clase para un cuerpo cilíndrico
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

/**
 * @class VerticalCylinderBody
 */
public class VerticalCylinderBody extends RigidBody {

    private float radius;

    /**
     * @brief Constructor de un VerticalCylinderBody
     * @param radius    Radio del cilindro (tiene altura infinita)
     */
    public VerticalCylinderBody(float radius) {
        this.radius = radius;
    }

    /**
     * @brief Comprueba la colisión con otro cuerpo
     * @param body  Cuerpo con el que comprobar la colisión
     * @param world Entorno de simulación
     * @return  Si se soporta la colisión con ese objeto
     */
    @Override
    public boolean checkHandleCollision(RigidBody body, World world) {

        if(body instanceof VerticalCylinderBody) {

            VerticalCylinderBody vcBody = (VerticalCylinderBody) body;

            float[] bodyPos = body.getPosition();

            float[] distanceVector = new float[2];
            distanceVector[0] = bodyPos[0] - position[0];
            distanceVector[1] = bodyPos[2] - position[2];

            float distanceSquared = distanceVector[0] * distanceVector[0] + distanceVector[1] * distanceVector[1];

            float radiusSum = this.getRadius() + vcBody.getRadius();
            float radiusSumSquared = radiusSum * radiusSum;

            // Comprueba la colisión
            if (distanceSquared < radiusSumSquared) {

                float distance = (float) Math.sqrt(distanceSquared);

                distanceVector[0] /= distance;
                distanceVector[1] /= distance;

                float overlap = (radiusSum - distance) / 2.0f;

                this.position[0] -= overlap * distanceVector[0];
                this.position[2] -= overlap * distanceVector[1];
                bodyPos[0] += overlap * distanceVector[0];
                bodyPos[2] += overlap * distanceVector[1];

                float tangentVectorX = -distanceVector[1];
                float tangentVectorZ = distanceVector[0];

                float[] bodyVelocity = body.getVelocity();
                float dotTan1 = this.velocity[0] * tangentVectorX + this.velocity[2] * tangentVectorZ;
                float dotTan2 = bodyVelocity[0] * tangentVectorX + bodyVelocity[2] * tangentVectorZ;

                float dotNorm1 = this.velocity[0] * distanceVector[0] + this.velocity[2] * distanceVector[1];
                float dotNorm2 = bodyVelocity[0] * distanceVector[0] + bodyVelocity[2] * distanceVector[1];

                float p1 = (dotNorm1 * (this.mass - body.getMass()) + 2.0f * body.getMass() * dotNorm2) / (this.mass + body.getMass());
                float p2 = (dotNorm2 * (body.getMass() - this.mass) + 2.0f * this.mass * dotNorm1) / (this.mass + body.getMass());

                this.velocity[0] = tangentVectorX * dotTan1 + distanceVector[0] * p1;
                this.velocity[2] = tangentVectorZ * dotTan1 + distanceVector[1] * p1;
                bodyVelocity[0] = tangentVectorX * dotTan2 + distanceVector[0] * p2;
                bodyVelocity[2] = tangentVectorZ * dotTan2 + distanceVector[1] * p2;

                // Se ha producido una colisión, llama al callback
                if(world.getCollisionCallback() != null) {
                    world.setLastCollision(this, body);
                    world.getCollisionCallback().run();
                }
            }

            return true;
        }

        return false;
    }

    /**
     * @brief Obtén el radio del cilindro
     * @return  El radio del cilindro
     */
    public float getRadius() {
        float maxScale = Math.max(scale[0], scale[1]);
        maxScale = Math.max(maxScale, scale[2]);
        return radius * maxScale;
    }
}
