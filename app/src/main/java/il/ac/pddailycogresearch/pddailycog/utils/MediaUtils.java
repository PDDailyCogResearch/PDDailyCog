package il.ac.pddailycogresearch.pddailycog.utils;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;

import il.ac.pddailycogresearch.pddailycog.R;

/**
 * Created by User on 08/02/2018.
 */

public class MediaUtils {
    public static MediaPlayer mpori;

    public static void stopMediaPlayer(FloatingActionButton soundButton) {
        if(mpori!=null) {
            mpori.stop();
            mpori.release();
            mpori = null;
            if(soundButton!=null){
            soundButton.setImageResource(R.drawable.sound_icon);}
        }
    }

    public static void stopMediaPlayer(Button soundButton) {
        if(mpori!=null) {
            mpori.stop();
            mpori.release();
            mpori = null;
            if(soundButton!=null){
                soundButton.setText(R.string.stop);}
        }
    }

    public static void toggleMediaPlayer(Context context, @RawRes int soundId, final FloatingActionButton soundButton) {

        if (mpori == null) {
            mpori = MediaPlayer.create(context, soundId);
        }
        if (mpori.isPlaying()) {
            mpori.pause();
            if(soundButton!=null){
            soundButton.setImageResource(R.drawable.sound_icon);}
        } else {
            if(soundButton!=null) {
                soundButton.setImageResource(R.drawable.stop_ic);
            }
            mpori.start();
            mpori.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopMediaPlayer(soundButton);
                        }
                    }
            );
        }
    }

    public static void toggleMediaPlayer(Context context, @RawRes int soundId, final Button soundButton) {

        if (mpori == null) {
            mpori = MediaPlayer.create(context, soundId);
        }
        if (mpori.isPlaying()) {
            mpori.pause();
            if(soundButton!=null){
                soundButton.setText(R.string.sound);}
        } else {
            if(soundButton!=null) {
                soundButton.setText(R.string.stop);
            }
            mpori.start();
            mpori.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopMediaPlayer(soundButton);
                        }
                    }
            );
        }
    }


    public static boolean isPlaying(){
        if(mpori!=null&&mpori.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

}
