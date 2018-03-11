package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.GoodByeActivity;
import il.ac.pddailycogresearch.pddailycog.adapters.ViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.DragListFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.InstructionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.PhotographFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.TextFragment;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.SoundManager;

public class TryChoreActivity extends BaseChoreActivity {

    private static final int CHORE_NUM = 1;
    private static final String TAG = TryChoreActivity.class.getSimpleName();
    private static final String POSITION_KEY = "position";

    protected final static int MAIN_LAYOUT_ID = R.layout.activity_try_chore;


    @BindView(R.id.buttonTrialChoreInstruction)
    Button buttonTrialChoreInstruction;

    private int position;
    private int instrcPressNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPagerActivity.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setButtonsVisibility();
            }
        });
    }

    @Override
    protected int getMainLayoutId() {
        return MAIN_LAYOUT_ID;
    }

    @Override
    protected ViewPagerAdapter createViewPagerAdapter() {
        return new ViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM) {

            @Override
            public Fragment getItem(int position) {
                Fragment currentFragment;

                switch (position) {
                    case 0:
                        currentFragment = InstructionFragment.newInstance(position, activityChoreNum, R.string.trail_chore_instruction);
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
        outState.putInt(POSITION_KEY, position);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.buttonTrialChoreInstruction)
    public void onViewClicked() {
        moveToInstrction();
    }


    private void moveToInstrction() {
        position = viewPagerActivity.getCurrentItem();
        instrcPressNum++;
        viewPagerActivity.setCurrentItem(0);
    }

    @Override
    protected void toggleSound() {
        int soundId = getResources().getIdentifier(
                Consts.TRIAL_CHORE_RAW_PREFIX + String.valueOf(viewPagerActivity.getCurrentItem()),
                "raw", getPackageName());
        SoundManager.getInstance().toggleMediaPlayer(getApplicationContext(), soundId, buttonSoundActivity);
    }

    @Override
    protected void moveNext(int currentPage) {
        if (currentPage != position) {//instruction clicked
            viewPagerActivity.setCurrentItem(position);
            //     setButtonsVisibility();
            return;
        }

        int nextPage = currentPage + 1;
        unenableNext();
        if (nextPage == adapter.getCount()) {
            completeChore();
            startActivity(new Intent(TryChoreActivity.this, GoodByeActivity.class));
        } else {
            viewPagerActivity.setCurrentItem(viewPagerActivity.getCurrentItem() + 1);
            position++;
            //     setButtonsVisibility();
        }
    }


    @Override
    public void onBackPressed() {
        //unable
    }

    @Override
    protected boolean moveWithDialogIfNeed(int currentPage) {
        return false;
    }

    private void setButtonsVisibility() {
        if (viewPagerActivity.getCurrentItem() == 0) {
            buttonSoundActivity.setVisibility(View.VISIBLE);
            buttonTrialChoreInstruction.setVisibility(View.GONE);
        } else {
            buttonSoundActivity.setVisibility(View.GONE);
            buttonTrialChoreInstruction.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void saveToDb() {
        super.saveToDb();
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "instrcPressNum", instrcPressNum);
        instrcPressNum = 0;
    }
}
