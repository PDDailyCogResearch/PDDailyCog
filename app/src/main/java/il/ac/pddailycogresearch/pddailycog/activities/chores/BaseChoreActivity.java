package il.ac.pddailycogresearch.pddailycog.activities.chores;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.adapters.BaseViewPagerAdapter;
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
    @BindView(R.id.buttonNext)
    Button buttonNextActivity;
    @BindView(R.id.buttonSound)
    FloatingActionButton buttonSoundActivity;


    protected BaseViewPagerAdapter adapter;
    protected int backPressNum;
    protected int soundPressNum;
    protected int exitPressNum;

    private long currentSessionStartTime;
    protected final static int MAIN_LAYOUT_ID = R.layout.activity_general_chore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getMainLayoutId());
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

    protected int getMainLayoutId() {
        return MAIN_LAYOUT_ID;
    }

    protected abstract BaseViewPagerAdapter createViewPagerAdapter();

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
                        onInitFromDbFinish();
                    }

                    @Override
                    public void onError(Exception e) {
                        CommonUtils.onGeneralError(e,TAG);
                    }
                });
    }

    protected void onInitFromDbFinish() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        CommonUtils.hideKeyboard(this);
        currentSessionStartTime = System.currentTimeMillis();
    }

    @OnClick({R.id.buttonNext, R.id.buttonSound, R.id.buttonExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonNext:
                SoundManager.getInstance().stopMediaPlayer(buttonSoundActivity);
                CommonUtils.hideKeyboard(this);
                int currentPage = viewPagerActivity.getCurrentItem();
                if (!moveWithDialogIfNeed(currentPage)) {
                    moveNext(currentPage);
                }
                break;
            case R.id.buttonSound:
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

    protected void completeChore() {
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
        SoundManager.getInstance().stopMediaPlayer(buttonSoundActivity);
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
        SoundManager.getInstance().stopMediaPlayer(buttonSoundActivity);
        super.onStop();
    }

    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, getChoreNum(), Consts.TIME_KEY_PREFIX + "total", elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    protected void saveToDb() {
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
        if (!buttonNextActivity.isEnabled()) {
            buttonNextActivity.setEnabled(true);
            buttonNextActivity.setBackgroundColor(ContextCompat.getColor(this,R.color.colorButtons));//(Color.parseColor("#4656AC"));
        }
    }

    @Override
    public void unenableNext() {
        if (buttonNextActivity.isEnabled()) {
            buttonNextActivity.setEnabled(false);
            buttonNextActivity.setBackgroundColor(ContextCompat.getColor(this,R.color.semi_gray));//Color.parseColor("#979797"));
        }
    }
    //endregion
}
