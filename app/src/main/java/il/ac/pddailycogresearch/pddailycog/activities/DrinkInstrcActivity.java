package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.MediaUtils;

public class DrinkInstrcActivity extends AppCompatActivity {
    private static final int CHORE_NUM = 2;

    @BindView(R.id.buttonSoundDrinkInstrcActivity)
    FloatingActionButton buttonSoundDrinkInstrcActivity;
    private int soundPressNum;
    private long currentSessionStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_instrc);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentSessionStartTime = System.currentTimeMillis();
    }

    @OnClick({R.id.buttonSoundDrinkInstrcActivity, R.id.buttonYesDrinkIstrcActivity, R.id.buttonNoDrinkIstrcActivity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonSoundDrinkInstrcActivity:
                MediaUtils.toggleMediaPlayer(getApplicationContext(), R.raw.drink_dialog_instrc, buttonSoundDrinkInstrcActivity);
                if (MediaUtils.isPlaying()) {
                    soundPressNum++;
                }
                break;
            case R.id.buttonYesDrinkIstrcActivity:

                String explantionWithSpeaker = getString(R.string.drink_instrc_dialog_explantion, Consts.SPEAKER_EMOJI);
                DialogUtils.createAlertWithSoundDialog(this, R.string.reminder, explantionWithSpeaker,
                        R.string.ok,  R.string.sound, android.R.string.cancel,
                        new IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener() {
                            @Override
                            public void onResult(int result, Button soundButton) {

                                switch (result){
                                    case -1:
                                        //finish(); or do nothing?...
                                        break;
                                    case 0:
                                        MediaUtils.toggleMediaPlayer(DrinkInstrcActivity.this,R.raw.drink_dialog_instrc,soundButton);
                                        break;
                                    case 1:
                                        startActivity(new Intent(DrinkInstrcActivity.this,DrinkChoreActivity.class));
                                        break;
                                }

                            }
                        }
                );
                break;
            case R.id.buttonNoDrinkIstrcActivity:
                DialogUtils.createAlertDialog(this, R.string.exit_alert_header, R.string.exit_alert_message,
                        android.R.string.ok, android.R.string.cancel,
                        new IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener() {
                            @Override
                            public void onResult(boolean result) {
                                if (result) {
                                    DialogUtils.createTurnOffAirplaneModeAlertDialog(DrinkInstrcActivity.this);
                                    // finish();
                                    // CommonUtils.closeApp(TrialChoreActivity.this);
                                }
                            }
                        });
                break;
        }
    }

    @Override
    protected void onStop() {
        MediaUtils.stopMediaPlayer(buttonSoundDrinkInstrcActivity);
        saveToDb();
        addTimeToDb();
        super.onStop();
    }

    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, Consts.TIME_KEY_PREFIX + "total", elapsedTime);
        //TODO add to time-0 also
        currentSessionStartTime = System.currentTimeMillis();
    }

    private void saveToDb() {
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "soundPressNum", soundPressNum);
        soundPressNum = 0;
    }
}
