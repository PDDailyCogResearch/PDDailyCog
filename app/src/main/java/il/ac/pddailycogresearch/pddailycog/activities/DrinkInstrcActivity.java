package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.chores.MainChoreActivity;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.ReadJsonUtil;
import il.ac.pddailycogresearch.pddailycog.utils.SoundManager;

public class DrinkInstrcActivity extends AppCompatActivity {
    //private static final int CHORE_NUM = 2;
    private static final String TAG = DrinkInstrcActivity.class.getSimpleName();
    private int CHORE_NUM;
    private static final String CHORE_NUM_NAME = "chore-num";

    @BindView(R.id.buttonSoundDrinkInstrcActivity)
    FloatingActionButton buttonSoundDrinkInstrcActivity;
    @BindView(R.id.textViewInstrcDrinkInstrcActivity)
    TextView textViewInstrcDrinkInstrcActivity;
    private int soundPressNum;
    private long currentSessionStartTime;
    private String choreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CHORE_NUM = getIntent().getIntExtra("chore_num",2);
        setContentView(R.layout.activity_drink_instrc);
        ButterKnife.bind(this);

        String filepath = Consts.ASSETS_PREFIX + CHORE_NUM + Consts.INSTRUCTION_FILENAME + ".json";
        String instrc = ReadJsonUtil.readStringByKey(this,filepath, Consts.OPENING_NAME);
        textViewInstrcDrinkInstrcActivity.setText(instrc);
        switch (CHORE_NUM){
            case 2:
                choreName = Consts.DRINK_CHORE_NAME_PREFIX;
            case 3:
                choreName = Consts.TRAVEL_CHORE_NAME_PREFIX;
                break;
            case 4:
                choreName = Consts.LIST_CHORE_NAME_PREFIX;
                break;
        }
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
                try {
                    int soundId = getResources().getIdentifier(
                            choreName + Consts.OPENING_NAME,
                            "raw", getPackageName());
                    SoundManager.getInstance().toggleMediaPlayer(getApplicationContext(), soundId, buttonSoundDrinkInstrcActivity);
                } catch (Exception e) {//TODO: replace with self made exception
                    CommonUtils.onGeneralError(e,TAG);
                    CommonUtils.showMessage(this,e.getMessage());
                }
                if (SoundManager.getInstance().isPlaying()) {
                    soundPressNum++;
                }
                break;
            case R.id.buttonYesDrinkIstrcActivity:
                SoundManager.getInstance().stopMediaPlayer(buttonSoundDrinkInstrcActivity);
                String explantionWithSpeaker = getString(R.string.drink_instrc_dialog_explantion, "");//Consts.SPEAKER_EMOJI);
                DialogUtils.createAlertDialogWithSound(this, explantionWithSpeaker,
                        R.string.ok, android.R.string.cancel, R.raw.drink_dialog_instrc,
                        new IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener() {
                            @Override
                            public void onResult(boolean result) {
                                if (result) {
//                                    switch (CHORE_NUM) {//TODO refactor this class? or find another way to reuse it
//                                        case 2:
//                                        startActivity(new Intent(DrinkInstrcActivity.this, DrinkChoreActivity.class));
//                                            break;
//                                        case 3:
//                                            startActivity(new Intent(DrinkInstrcActivity.this, ListChoreActivity.class));
//                                    }
                                    Intent newChore = new Intent(DrinkInstrcActivity.this, MainChoreActivity.class);
                                    newChore.putExtra(CHORE_NUM_NAME,CHORE_NUM);
                                    startActivity(newChore);

                                }
                            }

                            @Override
                            public void onSoundClick() {
                                soundPressNum++;
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
                                }
                            }
                        });
                break;
        }
    }

    @Override
    protected void onStop() {
        SoundManager.getInstance().stopMediaPlayer(buttonSoundDrinkInstrcActivity);
        saveToDb();
        addTimeToDb();
        super.onStop();
    }

    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, Consts.TIME_KEY_PREFIX + "total", elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    private void saveToDb() {
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "soundPressNum", soundPressNum);
        soundPressNum = 0;
    }
}
