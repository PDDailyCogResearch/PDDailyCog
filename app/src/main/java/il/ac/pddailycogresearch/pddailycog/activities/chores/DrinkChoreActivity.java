package il.ac.pddailycogresearch.pddailycog.activities.chores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.OpenQuestionnaireActivity;
import il.ac.pddailycogresearch.pddailycog.adapters.BaseViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.CheckBoxFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.DragListFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.PhotographFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.TextInputFragment;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.SoundManager;

public class DrinkChoreActivity extends BaseChoreActivity {

    private static final String TAG = DrinkChoreActivity.class.getSimpleName();
    private static final int CHORE_NUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BaseViewPagerAdapter createViewPagerAdapter() {
        return  new BaseViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM) {

            @Override
            public Fragment getItem(int position) {
                Fragment currentFragment;

                switch (position) {

                    case 5:
                        currentFragment = DragListFragment.newInstance(position, activityChoreNum,R.string.drag_drink_instr);
                        break;
                    case 6:
                        currentFragment = CheckBoxFragment.newInstance(position, activityChoreNum, R.string.drink_check_box_instrc);
                        break;
                    case 7:
                        currentFragment = TextInputFragment.newInstance(position, activityChoreNum, R.string.drink_first_text_instr);
                        break;
                    case 8:
                        currentFragment = PhotographFragment.newInstance(position, activityChoreNum, R.string.dring_photo_instr);
                        break;
                    case 11:
                        currentFragment = PhotographFragment.newInstance(position, activityChoreNum, R.string.dring_photo_dring_done);
                        break;
                    case 13:
                        currentFragment = TextInputFragment.newInstance(position, activityChoreNum, R.string.drink_time_valuat_text_instrc);
                        break;
                    default:
                        currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum, Consts.DRINK_CHORE_ASSETS_PREFIX);

                }
                return currentFragment;
            }

            @Override
            public int getCount() {
                return 14;
            }
        };
    }

    @Override
    protected int getChoreNum() {
        return CHORE_NUM;
    }

    @Override
    protected void toggleSound() {
        int soundId = getResources().getIdentifier(
                Consts.DRINK_CHORE_RAW_PREFIX + String.valueOf(viewPagerActivity.getCurrentItem()),
                "raw", getPackageName());
        try {
            SoundManager.getInstance().toggleMediaPlayer(getApplicationContext(), soundId, buttonSoundActivity);
        } catch (Exception e) {//TODO: replace with self made exception
            CommonUtils.onGeneralError(e,TAG);
            CommonUtils.showMessage(this,e.getMessage());
        }
    }

    @Override
    protected boolean moveWithDialogIfNeed(final int currentPage) {
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

    @Override
    protected void endActivity() {
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
    }
}
