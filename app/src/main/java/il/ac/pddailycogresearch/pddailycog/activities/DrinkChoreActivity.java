package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.GoodByeActivity;
import il.ac.pddailycogresearch.pddailycog.activities.simple.OpenQuestionnaireActivity;
import il.ac.pddailycogresearch.pddailycog.adapters.ViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.customviews.NonSwipeableViewPager;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.MediaUtils;
import il.ac.pddailycogresearch.pddailycog.utils.SoundManager;

public class DrinkChoreActivity extends AppCompatActivity implements
        RadioQuestionFragment.OnFragmentInteractionListener {

    private static final String TAG = DrinkChoreActivity.class.getSimpleName();
    private static final int CHORE_NUM = 2;

    @BindView(R.id.viewPagerDrinkActivity)
    NonSwipeableViewPager viewPagerDrinkActivity;
    @BindView(R.id.buttonNextDrinkActivity)
    Button buttonNextDrinkActivity;
    @BindView(R.id.buttonSoundDrinkActivity)
    FloatingActionButton buttonSoundDrinkActivity;
    @BindView(R.id.buttonExit)
    FloatingActionButton buttonExit;

    private ViewPagerAdapter adapter;
    //private MediaPlayer mpori;
    private int backPressNum;
    private int soundPressNum;
    private int exitPressNum;

    private long currentSessionStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_chore);
        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM);
        viewPagerDrinkActivity.setAdapter(adapter);
        viewPagerDrinkActivity.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (adapter.getPreviousFragment() != null) {
                    adapter.getPreviousFragment().onPageChanged(false);
                }
                if (adapter.getCurrentFragment() != null) {
                    adapter.getCurrentFragment().onPageChanged(true);
                }
            }
        });

        if (savedInstanceState == null) { //retrieve only if there isn't a saved state
            initFromDb();
        }
    }

    private void initFromDb() {
        FirebaseIO.getInstance().retreieveIntValueByKey(Consts.CHORES_KEY, CHORE_NUM, "position",
                new IOnFirebaseKeyValueListeners.OnIntValueListener() {
                    @Override
                    public void onValueRetrieved(Integer value) {
                        if (value == null) {
                            return;
                        }
                        viewPagerDrinkActivity.setCurrentItem(value);
                    }

                    @Override
                    public void onError(Exception e) {
                        CommonUtils.onGeneralError(e,TAG);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentSessionStartTime = System.currentTimeMillis();
    }

    @OnClick({R.id.buttonNextDrinkActivity, R.id.buttonSoundDrinkActivity, R.id.buttonExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonNextDrinkActivity:
                SoundManager.getInstance().stopMediaPlayer(buttonSoundDrinkActivity);
                CommonUtils.hideKeyboard(this);
                int currentPage = viewPagerDrinkActivity.getCurrentItem();
                if (!moveWithDialogIfNeed(currentPage)) {
                    moveNext(currentPage);
                }
                break;
            case R.id.buttonSoundDrinkActivity:
                int soundId = getResources().getIdentifier(
                        Consts.DRINK_CHORE_RAW_PREFIX + String.valueOf(viewPagerDrinkActivity.getCurrentItem()),
                        "raw", getPackageName());//TODO cut the files and correct the texts
                SoundManager.getInstance().toggleMediaPlayer(getApplicationContext(), soundId, buttonSoundDrinkActivity);
                if (SoundManager.getInstance().isPlaying()) {
                    soundPressNum++;
                }
                break;
            case R.id.buttonExit:
                exitPressNum++;
                showExitAlertDialog();
                break;
        }
    }

    private boolean moveWithDialogIfNeed(final int currentPage) {
        IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener resultListener = new IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    moveNext(currentPage);
                }
            }

            @Override
            public void onSoundClick() {
                soundPressNum++;
            }
        };
        int soundId = getResources().getIdentifier(
                Consts.DRINK_CHORE_DIALOG_RAW_PREFIX + String.valueOf(currentPage),
                "raw", getPackageName());
        if (currentPage == 3) {
            DialogUtils.createAlertDialogWithSound(this, R.string.drink_first_dialog_msg,
                    R.string.ok, android.R.string.cancel,soundId, resultListener);
            return true;
        }
        if (currentPage == 9) {
            DialogUtils.createAlertDialogWithSound(this, R.string.drink_second_dialog_msg,
                    R.string.finish,R.string.empty_string,soundId, resultListener);
            return true;
        }
        return false;
    }

    private void moveNext(int currentPage) {
        int nextPage = currentPage + 1;
        unenableNext();
        if (nextPage == adapter.getCount()) {
            DialogUtils.createAlertDialogWithSound(this, R.string.finish_drink, R.string.continue_,R.string.empty_string,
                    R.raw.drink_dialog_finish,
                    new IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener() {
                        @Override
                        public void onResult(boolean result) {
                            startActivity(new Intent(DrinkChoreActivity.this, OpenQuestionnaireActivity.class));
                        }

                        @Override
                        public void onSoundClick() {
                            soundPressNum++;
                        }
                    });
            completeChore();
        } else {
            viewPagerDrinkActivity.setCurrentItem(viewPagerDrinkActivity.getCurrentItem() + 1);
        }
    }

    private void completeChore() {
        FirebaseIO.getInstance().saveKeyValuePair(Consts.CHORES_KEY, CHORE_NUM,
                Consts.IS_COMPLETED_KEY, true);
    }

    private void showExitAlertDialog() {
        DialogUtils.createAlertDialog(this, R.string.exit_alert_header, R.string.exit_alert_message,
                android.R.string.ok, android.R.string.cancel,
                new IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            DialogUtils.createTurnOffAirplaneModeAlertDialog(DrinkChoreActivity.this);
                            // finish();
                            // CommonUtils.closeApp(TrialChoreActivity.this);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        backPressNum++;
        SoundManager.getInstance().stopMediaPlayer(buttonSoundDrinkActivity);
        int prevItem = viewPagerDrinkActivity.getCurrentItem() - 1;
        if (prevItem < 0) {
            super.onBackPressed();
        } else {
            viewPagerDrinkActivity.setCurrentItem(prevItem);
        }
    }

    @Override
    protected void onStop() {
        saveToDb();
        addTimeToDb();
        SoundManager.getInstance().stopMediaPlayer(buttonSoundDrinkActivity);
        super.onStop();
    }

    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, Consts.TIME_KEY_PREFIX + "total", elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    private void saveToDb() {
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "backPressNum", backPressNum);
        backPressNum = 0;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "soundPressNum", soundPressNum);
        soundPressNum = 0;
        FirebaseIO.getInstance().saveKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "position", viewPagerDrinkActivity.getCurrentItem());
    }

    //region fragment callbacks
    @Override
    public void enableNext() {
        if (!buttonNextDrinkActivity.isEnabled()) {
            buttonNextDrinkActivity.setEnabled(true);
            buttonNextDrinkActivity.setBackgroundColor(Color.parseColor("#4656AC"));
        }
    }

    @Override
    public void unenableNext() {
        if (buttonNextDrinkActivity.isEnabled()) {
            buttonNextDrinkActivity.setEnabled(false);
            buttonNextDrinkActivity.setBackgroundColor(Color.parseColor("#979797"));
        }
    }
    //endregion
}
