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
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.CheckBoxFragment;
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

public class TryChoreActivity extends BaseChoreActivity{

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
    }

    @Override
    protected ViewPagerAdapter createViewPagerAdapter() {
        return  new ViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM) {

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
    }

    @Override
    protected int getChoreNum() {
            return CHORE_NUM;
        }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION_KEY,position);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void toggleSound() {
        int soundId = getResources().getIdentifier(
                Consts.TRIAL_CHORE_RAW_PREFIX + String.valueOf(viewPagerActivity.getCurrentItem()),
                "raw", getPackageName());
        SoundManager.getInstance().toggleMediaPlayer(getApplicationContext(), soundId, buttonSoundDrinkActivity);
    }

    @Override
    protected boolean moveWithDialogIfNeed(int currentPage) {
        return false;
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

   }
