package com.dam.chapas.online;

/**
 * @file MovePdu.java
 * @brief PDU enviada entre jugadores
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import com.dam.chapas.bluetooth.BluetoothService;

/**
 * @class MovePdu
 */
public class MovePdu {

    private int frame;
    private float directionX;
    private float directionZ;
    private int capID;
    private int expired;
    private int shoots;
    private int turnTime;

    /**
     * @brief Constructor de MovePdu
     */
    public MovePdu() {
        frame = 0;
        directionX = 0.0f;
        directionZ = 0.0f;
        capID = 0;
        expired = 0;
        shoots = 0;
        turnTime = 0;
    }

    /**
     * @brief Obtén el número de tiros restantes
     * @return  El número de tiros restantes
     */
    public int getShoots() {
        return shoots;
    }

    /**
     * @brief Establece el número de tiros restantes
     * @param shoots    El número de tiros restantes
     */
    public void setShoots(int shoots) {
        this.shoots = shoots;
    }

    /**
     * @brief Obtén el tiempo en el que ocurre este movimiento
     * @return  El tiempo en el que ocurre este movimiento
     */
    public int getFrame() {
        return frame;
    }

    /**
     * @brief Establece el tiempo en el que ocurre este movimiento
     * @param frame El tiempo en el que ocurre este movimiento
     */
    public void setFrame(int frame) {
        this.frame = frame;
    }

    /**
     * @brief Obtén el impulso en el eje X
     * @return  El impulso en el eje X
     */
    public float getDirectionX() {
        return directionX;
    }

    /**
     * @brief Establece el impulso en el eje X
     * @param directionX    El impulso en el eje X
     */
    public void setDirectionX(float directionX) {
        this.directionX = directionX;
    }

    /**
     * @brief Obtén el impulso en el eje Z
     * @return  El impulso en el eje Z
     */
    public float getDirectionZ() {
        return directionZ;
    }

    /**
     * @brief Establece el impulso en el eje Z
     * @param directionZ    El impulso en el eje Z
     */
    public void setDirectionZ(float directionZ) {
        this.directionZ = directionZ;
    }

    /**
     * @brief Obtén la chapa que realiza el movimiento
     * @return  La chapa que realiza el movimiento
     */
    public int getCapID() {
        return capID;
    }

    /**
     * @brief Establece la chapa que realiza el movimiento
     * @param capID La chapa que realiza el movimiento
     */
    public void setCapID(int capID) {
        this.capID = capID;
    }

    /**
     * @brief Obtén si se ha acabado el tiempo
     * @return  Si se ha acabado el tiempo
     */
    public int getExpired() {
        return expired;
    }

    /**
     * @brief Establece si se ha acabado el tiempo
     * @param expired   Si se ha acabado el tiempo
     */
    public void setExpired(int expired) {
        this.expired = expired;
    }

    /**
     * @brief Obtén el tiempo de turno restante
     * @return  El tiempo de turno restante
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * @brief Establece el tiempo de turno restante
     * @param turnTime  El tiempo de turno restante
     */
    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    /**
     * @brief Envía la PDU al otro jugador
     * @param service   El servicio Bluetooth
     */
    public void send(BluetoothService service) {
        byte[] data = new byte[28];
        setInt(data, 0, frame);
        setFloat(data, 4, directionX);
        setFloat(data, 8, directionZ);
        setInt(data, 12, capID);
        setInt(data, 16, expired);
        setInt(data, 20, shoots);
        setInt(data, 24, turnTime);
        service.write(data);
    }

    /**
     * @brief Forma una PDU con datos recibidos de un socket
     * @param data  Los datos recibidos
     */
    public void recv(byte[] data) {
        frame = getInt(data, 0);
        directionX = getFloat(data, 4);
        directionZ = getFloat(data, 8);
        capID = getInt(data, 12);
        expired = getInt(data, 16);
        shoots = getInt(data, 20);
        turnTime = getInt(data, 24);
    }

    /**
     * @brief Guarda un entero en un buffer
     * @param data      Buffer a usar
     * @param offset    Índice del buffer
     * @param value     Valor del entero
     */
    private static void setInt(byte[] data, int offset, int value) {
        data[offset + 0] = (byte)((value >> 24) &0xFF);
        data[offset + 1] = (byte)((value >> 16) &0xFF);
        data[offset + 2] = (byte)((value >> 8) &0xFF);
        data[offset + 3] = (byte)(value &0xFF);
    }

    /**
     * @brief Guarda un float en un buffer
     * @param data      Buffer a usar
     * @param offset    Índice del buffer
     * @param value     Valor del float
     */
    private static void setFloat(byte[] data, int offset, float value) {
        setInt(data, offset, Float.floatToIntBits(value));
    }

    /**
     * @brief Obtén un entero de un buffer
     * @param data      Buffer a usar
     * @param offset    Índice del buffer
     * @return  El entero guardado
     */
    private static int getInt(byte[] data, int offset) {
        return ((data[offset + 0] &0xFF) << 24) |
                ((data[offset + 1] &0xFF) << 16) |
                ((data[offset + 2] &0xFF) << 8) |
                (data[offset + 3] &0xFF);
    }

    /**
     * @brief Obtén un float de un buffer
     * @param data      Buffer a usar
     * @param offset    Índice del buffer
     * @return  El float guardado
     */
    private static float getFloat(byte[] data, int offset) {
        return Float.intBitsToFloat(getInt(data, offset));
    }
}
