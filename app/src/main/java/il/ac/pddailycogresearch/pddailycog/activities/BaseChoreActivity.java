package il.ac.pddailycogresearch.pddailycog.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.adapters.ViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.customviews.NonSwipeableViewPager;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.BaseViewPagerFragment;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.SoundManager;

/**
 * Created by User on 08/03/2018.
 */

public abstract class BaseChoreActivity extends AppCompatActivity implements
        BaseViewPagerFragment.OnFragmentInteractionListener {

    protected static final String TAG = DrinkChoreActivity.class.getSimpleName();


    @BindView(R.id.viewPagerActivity)
    NonSwipeableViewPager viewPagerActivity;
    @BindView(R.id.buttonNextDrinkActivity)
    Button buttonNextDrinkActivity;
    @BindView(R.id.buttonSoundDrinkActivity)
    FloatingActionButton buttonSoundDrinkActivity;


    protected ViewPagerAdapter adapter;
    protected int backPressNum;
    protected int soundPressNum;
    protected int exitPressNum;

    private long currentSessionStartTime;
    protected final static int MAIN_LAYOUT_ID = R.layout.activity_drink_chore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MAIN_LAYOUT_ID);
        ButterKnife.bind(this);

        if (savedInstanceState == null) { //retrieve only if there isn't a saved state
            initFromDb();
        }

        adapter = createViewPagerAdapter();
        viewPagerActivity.setAdapter(adapter);
        viewPagerActivity.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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


    }

    protected abstract ViewPagerAdapter createViewPagerAdapter();

    protected abstract int getChoreNum();

    protected void initFromDb() {
        FirebaseIO.getInstance().retreieveIntValueByKey(Consts.CHORES_KEY, getChoreNum(), Consts.POSITION,
                new IOnFirebaseKeyValueListeners.OnIntValueListener() {
                    @Override
                    public void onValueRetrieved(Integer value) {
                        if (value == null) {
                            return;
                        }
                        viewPagerActivity.setCurrentItem(value);
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
                int currentPage = viewPagerActivity.getCurrentItem();
                if (!moveWithDialogIfNeed(currentPage)) {
                    moveNext(currentPage);
                }
                break;
            case R.id.buttonSoundDrinkActivity:
                toggleSound();
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

    protected abstract void toggleSound();

    protected abstract boolean moveWithDialogIfNeed(final int currentPage);

    protected void moveNext(int currentPage) {
        int nextPage = currentPage + 1;
        unenableNext();
        if (nextPage == adapter.getCount()) {
            endActivity();
            completeChore();
        } else {
            viewPagerActivity.setCurrentItem(viewPagerActivity.getCurrentItem() + 1);
        }
    }

    protected  void endActivity(){
        //empty implemention
    }

    private void completeChore() {
        FirebaseIO.getInstance().saveKeyValuePair(Consts.CHORES_KEY, getChoreNum(),
                Consts.IS_COMPLETED_KEY, true);
    }

    protected void showExitAlertDialog() {
        DialogUtils.createAlertDialog(this, R.string.exit_alert_header, R.string.exit_alert_message,
                android.R.string.ok, android.R.string.cancel,
                new IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            DialogUtils.createTurnOffAirplaneModeAlertDialog(BaseChoreActivity.this);

                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        backPressNum++;
        SoundManager.getInstance().stopMediaPlayer(buttonSoundDrinkActivity);
        int prevItem = viewPagerActivity.getCurrentItem() - 1;
        if (prevItem < 0) {
            super.onBackPressed();
        } else {
            viewPagerActivity.setCurrentItem(prevItem);
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
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, getChoreNum(), Consts.TIME_KEY_PREFIX + "total", elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    private void saveToDb() {
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, getChoreNum(), "backPressNum", backPressNum);
        backPressNum = 0;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, getChoreNum(), "soundPressNum", soundPressNum);
        soundPressNum = 0;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, getChoreNum(), "exitPressNum", exitPressNum);
        exitPressNum = 0;
        FirebaseIO.getInstance().saveKeyValuePair(Consts.CHORES_KEY, getChoreNum(), "position", viewPagerActivity.getCurrentItem());
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
