package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Assets {
    public static boolean playSounds = true;

    private static Sound backgroundsound;
    private static Sound idubbbz;
    private static Sound golfballHit;
    private static Sound crowd;
    static boolean soundworks = true;
    static float ballSoundMultiplier = 1;
    static float CheeringMultiplier = 1;

    public static void  init(){
        //try {
            backgroundsound = Gdx.audio.newSound(Gdx.files.internal("Sounds/BackgroundSound.mp3"));
            idubbbz = Gdx.audio.newSound(Gdx.files.internal("Sounds/idubbbz.mp3"));
            golfballHit  = Gdx.audio.newSound(Gdx.files.internal("Sounds/golfballhit.mp3"));
            crowd  = Gdx.audio.newSound(Gdx.files.internal("Sounds/crowd.mp3"));

        //}catch(Exception e){
        //    soundworks = false;
        //}

        if(playSounds){
            System.out.println("initializing backgroundsound");
            backgroundsound.loop(0.5f);
            crowd.loop(0.001f);
        }

    }

    public static void setCheerVolume(float distance){
        float volume = distance * CheeringMultiplier;
        if(volume >= 1)volume = 1;
        //TODO: fix sooundid? Don't know how to find it and how to know which id corrosponds to which sound
        crowd.setVolume(4,volume);
    }

    public static void Scored(){
        if(soundworks) {
            idubbbz.play();
        }
    }

    public static void ballHit(){
        if(soundworks){
            golfballHit.play();
        }
    }

    public static void ballHit(float howhard){
        howhard*=ballSoundMultiplier;
        if (howhard >=1){
            howhard = 1;
        }
        if(soundworks){
            golfballHit.play(howhard);
        }
    }

    public static void dispose(){
        backgroundsound.dispose();
        idubbbz.dispose();
        golfballHit.dispose();
        crowd.dispose();
    }
}
