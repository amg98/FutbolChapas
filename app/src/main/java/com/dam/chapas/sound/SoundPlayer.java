package com.dam.chapas.sound;

/**
 * @file SoundPlayer.java
 * @brief Clase para la gestión de sonidos y músicas de fondo
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.dam.chapas.R;

/**
 * @class SoundPlayer
 */
public class SoundPlayer {

    private SoundPool soundPool;
    private MediaPlayer ambientSound;   /**< Sonido de fondo durante el partido */
    private int crowdSound;             /**< Sonido de la grada al marcar un gol */
    private int goalSound;              /**< Sonido de la porteria al marcar un gol */
    private int kickSound;              /**< Sonido al lanzar la chapa */
    private int reboundSound;           /**< Sonido al rebotar con alguna chapa */
    private int whistleSound;           /**< Sonido del pitido del árbitro */

    /**
     * @brief Constructor de un SoundPlayer
     * @param context   Contexto de la aplicación
     */
    public SoundPlayer(Context context){

        // SoundPool (int maxStreams, int streamType, int srcQuality)
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        // Cargamos los sonidos
        ambientSound = MediaPlayer.create(context, R.raw.ambient);
        ambientSound.setLooping(true);
        crowdSound = soundPool.load(context, R.raw.crowd, 1);
        goalSound = soundPool.load(context, R.raw.goal, 1);
        kickSound = soundPool.load(context, R.raw.kick, 1);
        reboundSound = soundPool.load(context, R.raw.rebound, 1);
        whistleSound = soundPool.load(context, R.raw.whistle, 1);
    }

    /**
     * @brief Libera los recursos del SoundPlayer
     */
    public void free() {
        ambientSound.stop();
        ambientSound.release();
        soundPool.release();
    }

    /**
     * @brief Reproduce la música ambiental
     */
    public void playAmbientSound() {
        ambientSound.start();
    }

    /**
     * @brief Reproduce el ruido de la afición
     */
    public void playCrowdSound() {
        soundPool.play(crowdSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * @brief Reproduce el sonido de GOL
     */
    public void playGoalSound() {
        soundPool.play(goalSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * @brief Reproduce el sonido de una patada
     */
    public void playKickSound() {
        soundPool.play(kickSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * @brief Reproduce el sonido de un pase
     */
    public void playReboundSound() {
        soundPool.play(reboundSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * @brief Reproduce el sonido de un pitido
     */
    public void playWhistleSound() { soundPool.play(whistleSound, 1.0f, 1.0f, 1, 0, 1.0f); }
}
