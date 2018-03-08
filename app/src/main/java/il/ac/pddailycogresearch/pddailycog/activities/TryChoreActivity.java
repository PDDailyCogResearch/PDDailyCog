package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.GoodByeActivity;
import il.ac.pddailycogresearch.pddailycog.adapters.ViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.customviews.NonSwipeableViewPager;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.BaseViewPagerFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.DragListFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.InstructionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.PhotographFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.TextFragment;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.SoundManager;

public class TryChoreActivity extends AppCompatActivity implements
        BaseViewPagerFragment.OnFragmentInteractionListener {

    private static final int CHORE_NUM = 1;
    private static final String TAG = TryChoreActivity.class.getSimpleName();
    private static final String POSITION_KEY = "position";

    @BindView(R.id.viewPagerActivity)
    NonSwipeableViewPager viewPagerDrinkActivity;
    @BindView(R.id.buttonNextDrinkActivity)
    Button buttonNextDrinkActivity;
    @BindView(R.id.buttonTrialChoreInstruction)
    Button buttonTrialChoreInstruction;
    @BindView(R.id.buttonSoundDrinkActivity)
    FloatingActionButton buttonSoundDrinkActivity;
    @BindView(R.id.buttonExit)
    FloatingActionButton buttonExit;

    private ViewPagerAdapter adapter;
    private int instrcPressNum;
    private int soundPressNum;
    private int exitPressNum;

    private int position;

    private long currentSessionStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_chore);
        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM){

            @Override
            public Fragment getItem(int position) {
                Fragment currentFragment;

                switch (position) {
                    case 0:
                        currentFragment = InstructionFragment.newInstance(position, activityChoreNum, R.string.trail_chore_instruction);//TODO creae new text frahment
                        break;
                    case 1:
                        currentFragment = PhotographFragment.newInstance(position, activityChoreNum, R.string.now_take_photo);
                        break;
                    case 2:
                        currentFragment = TextFragment.newInstance(position, activityChoreNum, R.string.text_input_instrc);
                        break;
                    case 3:
                        currentFragment = DragListFragment.newInstance(position, activityChoreNum, R.string.order_numbers_instrc);
                        break;
                    case 4:
                        currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum, Consts.TRIAL_CHORE_ASSETS_PREFIX);
                        break;
                    default:
                        currentFragment = null;
                }
                return currentFragment;
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
        viewPagerDrinkActivity.setAdapter(adapter);
        viewPagerDrinkActivity.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setButtonsVisibility();
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
        } else {
            position = savedInstanceState.getInt(POSITION_KEY);
        }
    }

    private void initFromDb() {
        FirebaseIO.getInstance().retreieveIntValueByKey(Consts.CHORES_KEY, CHORE_NUM, "position",
                new IOnFirebaseKeyValueListeners.OnIntValueListener() {
                    @Override
                    public void onValueRetrieved(Integer value) {
                        if (value == null) {
                            position=0;
                            return;
                        }
                        position=value;
                        viewPagerDrinkActivity.setCurrentItem(value);
                    }

                    @Override
                    public void onError(Exception e) {
                        CommonUtils.onGeneralError(e, TAG);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentSessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION_KEY,position);
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.buttonNextDrinkActivity, R.id.buttonSoundDrinkActivity, R.id.buttonExit,R.id.buttonTrialChoreInstruction})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonTrialChoreInstruction:
                moveToInstrction();
                break;
            case R.id.buttonNextDrinkActivity:
                SoundManager.getInstance().stopMediaPlayer(buttonSoundDrinkActivity);
                CommonUtils.hideKeyboard(this);
                int currentPage = viewPagerDrinkActivity.getCurrentItem();
                //if (!moveWithDialogIfNeed(currentPage)) {
                moveNext(currentPage);
                // }
                break;
            case R.id.buttonSoundDrinkActivity:
                int soundId = getResources().getIdentifier(
                        Consts.TRIAL_CHORE_RAW_PREFIX + String.valueOf(0),
                        "raw", getPackageName());
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

    private void moveToInstrction() {
        position = viewPagerDrinkActivity.getCurrentItem();
        instrcPressNum++;
        viewPagerDrinkActivity.setCurrentItem(0);
    }


    private void moveNext(int currentPage) {
        if(currentPage!=position){//instruction clicked
            viewPagerDrinkActivity.setCurrentItem(position);
       //     setButtonsVisibility();
            return;
        }

        int nextPage = currentPage + 1;
        unenableNext();
        if (nextPage == adapter.getCount()) {
            completeChore();
            startActivity(new Intent(TryChoreActivity.this, GoodByeActivity.class));
        } else {
            viewPagerDrinkActivity.setCurrentItem(viewPagerDrinkActivity.getCurrentItem() + 1);
            position++;
       //     setButtonsVisibility();
        }
    }

    private void setButtonsVisibility() {
        if(viewPagerDrinkActivity.getCurrentItem()==0) {
            buttonSoundDrinkActivity.setVisibility(View.VISIBLE);
            buttonTrialChoreInstruction.setVisibility(View.GONE);
        }
        else {
            buttonSoundDrinkActivity.setVisibility(View.GONE);
            buttonTrialChoreInstruction.setVisibility(View.VISIBLE);
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
                            DialogUtils.createTurnOffAirplaneModeAlertDialog(TryChoreActivity.this);
                            // finish();
                            // CommonUtils.closeApp(TrialChoreActivity.this);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //unable
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
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "instrcPressNum", instrcPressNum);
        instrcPressNum = 0;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "soundPressNum", soundPressNum);
        soundPressNum = 0;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "exitPressNum", exitPressNum);
        exitPressNum = 0;
        FirebaseIO.getInstance().saveKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "position", position);
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
}
