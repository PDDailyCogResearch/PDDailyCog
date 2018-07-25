package il.ac.pddailycogresearch.pddailycog.activities.chores;

import android.content.Intent;
import android.support.v4.app.Fragment;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.GoodByeActivity;
import il.ac.pddailycogresearch.pddailycog.activities.simple.OpenQuestionnaireActivity;
import il.ac.pddailycogresearch.pddailycog.adapters.BaseViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.CheckBoxFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.DragListFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.PhotographFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.TextInputFragment;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;

/**
 * Created by shna0 on 18/07/2018.
 */

public class ListChoreActivity extends BaseChoreActivity {
    private static final String TAG = ListChoreActivity.class.getSimpleName();
    private static final int CHORE_NUM = 3;

    @Override
    protected BaseViewPagerAdapter createViewPagerAdapter() {
        return  new BaseViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM) {

            @Override
            public Fragment getItem(int position) {
                Fragment currentFragment;

                switch (position) {

                    case 5:
                        currentFragment = DragListFragment.newInstance(position, activityChoreNum, R.string.drag_list_instr);
                        break;
                    case 6:
                        currentFragment = CheckBoxFragment.newInstance(position, activityChoreNum, R.string.list_check_box_instrc);
                        break;
                    case 7:
                        currentFragment = TextInputFragment.newInstance(position, activityChoreNum, R.string.list_first_text_instr);
                        break;
                    case 8:
                        currentFragment = PhotographFragment.newInstance(position, activityChoreNum, R.string.list_photo_instr);
                        break;
                    case 11:
                        currentFragment = PhotographFragment.newInstance(position, activityChoreNum, R.string.list_photo_list_done).setEnableNext(true);
                        break;
                    case 13:
                        currentFragment = TextInputFragment.newInstance(position, activityChoreNum, R.string.drink_time_valuat_text_instrc)
                                .setMinutesVisible(true);
                        break;
                    default:
                        currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum, Consts.LIST_CHORE_ASSETS_PREFIX);

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
            DialogUtils.createAlertDialogWithSound(this, R.string.list_first_dialog_msg,
                    R.string.ok, android.R.string.cancel,soundId, resultListener);
            return true;
        }
        if (currentPage == 9) {
            DialogUtils.createAlertDialogWithSound(this, R.string.list_second_dialog_msg,
                    R.string.finish,R.string.empty_string,soundId, resultListener);
            return true;
        }
        return false;
    }

    protected void endActivity() {
        DialogUtils.createAlertDialogWithSound(this, R.string.finish_list, R.string.continue_,R.string.empty_string,
                R.raw.drink_dialog_finish,
                new IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        startActivity(new Intent(ListChoreActivity.this, GoodByeActivity.class));
                    }

                    @Override
                    public void onSoundClick() {
                        soundPressNum++;
                    }
                });
    }
}
