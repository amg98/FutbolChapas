package com.dam.chapas.physics;

/**
 * @file WallBody.java
 * @brief Clase para una pared
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

/**
 * @class WallBody
 */
public class WallBody extends RigidBody {

    private float startX;
    private float startZ;
    private float endX;
    private float endZ;
    private float thick;

    /**
     * @brief Constructor de una pared
     * @param startX    Punto inicial X
     * @param startZ    Punto inicial Z (y = 0)
     * @param endX      Punto final X
     * @param endZ      Punto final Z (y = 0)
     * @param thick     Grosor de la pared
     */
    public WallBody(float startX, float startZ, float endX, float endZ, float thick) {
        this.startX = startX;
        this.startZ = startZ;
        this.endX = endX;
        this.endZ = endZ;
        this.thick = thick;
    }

    /**
     * @brief Comprueba la colisión con otro cuerpo
     * @param body  Cuerpo con el que comprobar la colisión
     * @param world Entorno de simulación
     * @return  Si se soporta la colisión con este cuerpo
     */
    @Override
    public boolean checkHandleCollision(RigidBody body, World world) {

        if(body instanceof VerticalCylinderBody) {

            VerticalCylinderBody vcBody = (VerticalCylinderBody) body;
            float[] bodyPos = body.getPosition();

            float lineX1 = endX - startX;
            float lineY1 = endZ - startZ;
            float lineX2 = bodyPos[0] - startX;
            float lineY2 = bodyPos[2] - startZ;

            float edgeLength = lineX1 * lineX1 + lineY1 * lineY1;
            float dot = lineX1 * lineX2 + lineY1 * lineY2;
            float t = Math.max(0, Math.min(edgeLength, dot)) / edgeLength;

            float closestPointX = startX + t * lineX1;
            float closestPointY = startZ + t * lineY1;

            float distance = (bodyPos[0] - closestPointX);
            distance *= distance;
            float dist2 = (bodyPos[2] - closestPointY);
            dist2 *= dist2;
            distance += dist2;

            // Comprueba la colisión
            if(distance <= (thick + vcBody.getRadius()) * (thick + vcBody.getRadius())) {
                VerticalCylinderBody fakeBody = new VerticalCylinderBody(thick);
                fakeBody.setMass(body.getMass());
                fakeBody.setPosition(closestPointX, 0.0f, closestPointY);
                fakeBody.setVelocity(-body.getVelocity()[0], -body.getVelocity()[1], -body.getVelocity()[2]);

                fakeBody.checkHandleCollision(body, world);
            }

            return true;
        }

        return false;
    }
}
