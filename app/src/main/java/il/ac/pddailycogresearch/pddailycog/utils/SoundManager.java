package il.ac.pddailycogresearch.pddailycog.utils;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;

import il.ac.pddailycogresearch.pddailycog.R;

/**
 * Created by User on 18/02/2018.
 */

public class SoundManager {
    private static final String TAG = SoundManager.class.getSimpleName();
    private static SoundManager mInstance;

    private final String buttonStartText = Consts.SPEAKER_EMOJI;
    private final String buttonStopText = Consts.PAUSE_EMOJI;

    private MediaPlayer mpori;
    private Button soundButton;
    private FloatingActionButton soundFloatingButton;


    private SoundManager() {

    }

    public static SoundManager getInstance() {
        if (mInstance == null) {
            mInstance = new SoundManager();
        }
        return mInstance;
    }

    public void stopMediaPlayer(FloatingActionButton soundFloatingButton) {
        this.soundFloatingButton = soundFloatingButton;
        if (mpori != null) {
            mpori.stop();
            mpori.release();
            mpori = null;
            showStart();
        }
    }

    public void stopMediaPlayer(Button soundButton) {
        this.soundButton=soundButton;
        if (mpori != null) {
            mpori.stop();
            mpori.release();
            mpori = null;
            showStart();
        }
    }

    public void toggleMediaPlayer(Context context, @RawRes int soundId, final FloatingActionButton soundFloatingButton) throws Exception {
        this.soundFloatingButton = soundFloatingButton;
        if (mpori == null) {
            try {
                mpori = MediaPlayer.create(context, soundId);
            } catch (Resources.NotFoundException e){
                throw new Exception(context.getResources().getString(R.string.sound_error));
            }
        }
        if (mpori.isPlaying()) {
            mpori.pause();
            showStart();
        } else {
            showStop();
            mpori.start();
            mpori.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopMediaPlayer(soundFloatingButton);
                        }
                    }
            );
        }
    }

    public void toggleMediaPlayer(Context context, @RawRes int soundId, final Button soundButton) {
        this.soundButton = soundButton;
        if (mpori == null) {
            mpori = MediaPlayer.create(context, soundId);
        }
        if (mpori.isPlaying()) {
            mpori.pause();
            showStart();
        } else {
            showStop();
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

    private void showStart() {
        if (soundButton != null) {
            soundButton.setText(buttonStartText);
        }
        if (soundFloatingButton != null) {
            soundFloatingButton.setImageResource(R.drawable.sound_icon);
        }
    }

    private void showStop() {
        if (soundButton != null) {
            soundButton.setText(buttonStopText);
        }
        if (soundFloatingButton != null) {
            soundFloatingButton.setImageResource(R.drawable.stop_ic);

        }
    }

    public boolean isPlaying() {
        if (mpori != null && mpori.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }
}
