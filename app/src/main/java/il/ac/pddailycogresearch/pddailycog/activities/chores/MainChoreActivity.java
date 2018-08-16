package il.ac.pddailycogresearch.pddailycog.activities.chores;

import android.content.Intent;
import android.os.Bundle;
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
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.ReadJsonUtil;
import il.ac.pddailycogresearch.pddailycog.utils.SoundManager;

/**
 * Created by shna0 on 15/08/2018.
 */

public class MainChoreActivity extends BaseChoreActivity {

    private static final String TAG = MainChoreActivity.class.getSimpleName();
    private static final String CHORE_NUM_NAME = "chore-num";
    //   private static final int CHORE_NUM = 2;
    protected int choreNum;
    private String choreRawPrefix;
    private String dialogRawPrefix;
    private boolean openQuestionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        choreNum = getIntent().getIntExtra(CHORE_NUM_NAME,2); //TODO replace with base's activityChoreNum

        super.onCreate(savedInstanceState);

        switch (choreNum){
            case 1:
                choreRawPrefix = Consts.TRIAL_CHORE_RAW_PREFIX;
                break;
            case 2:
                choreRawPrefix = Consts.DRINK_CHORE_RAW_PREFIX;
                dialogRawPrefix = Consts.DRINK_CHORE_DIALOG_RAW_PREFIX;
                openQuestionnaire = true;
                break;
            case 3:
                choreRawPrefix = Consts.LIST_CHORE_RAW_PREFIX;
                break;
        }
    }

    @Override
    protected BaseViewPagerAdapter createViewPagerAdapter() {
        return  new BaseViewPagerAdapter(getSupportFragmentManager(), choreNum) {

            @Override
            public Fragment getItem(int position) {
                Fragment currentFragment;

                switch (position) {

                    case 5:
                        currentFragment = DragListFragment.newInstance(position, activityChoreNum);
                        break;
                    case 6:
                        currentFragment = CheckBoxFragment.newInstance(position, activityChoreNum);
                        break;
                    case 7:
                        currentFragment = TextInputFragment.newInstance(position, activityChoreNum);
                        break;
                    case 8:
                        currentFragment = PhotographFragment.newInstance(position, activityChoreNum);
                        break;
                    case 11:
                        currentFragment = PhotographFragment.newInstance(position, activityChoreNum).setEnableNext(true);
                        break;
                    case 13:
                        currentFragment = TextInputFragment.newInstance(position, activityChoreNum)
                                .setMinutesVisible(true);
                        break;
                    default:
                        currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum);

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
        return choreNum;
    }

    @Override
    protected void toggleSound() {
        int soundId = getResources().getIdentifier(
                choreRawPrefix + String.valueOf(viewPagerActivity.getCurrentItem()),
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
                dialogRawPrefix + String.valueOf(currentPage),
                "raw", getPackageName());
        String dialogMsg = ReadJsonUtil.readDialogInstruction(this,choreNum,String.valueOf(currentPage));
        if (currentPage == 3) {
            DialogUtils.createAlertDialogWithSound(this, dialogMsg,
                    R.string.ok, android.R.string.cancel,soundId, resultListener);
            return true;
        }
        if (currentPage == 9) {
            DialogUtils.createAlertDialogWithSound(this, dialogMsg,
                    R.string.finish,R.string.empty_string,soundId, resultListener);
            return true;
        }
        return false;
    }

    @Override
    protected void endActivity() {
        String msg = ReadJsonUtil.readDialogInstruction(this,choreNum,Consts.FINISH_MSG_KEY);
        DialogUtils.createAlertDialogWithSound(this, msg, R.string.continue_,R.string.empty_string,
                R.raw.drink_dialog_finish,
                new IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if(openQuestionnaire) {
                            startActivity(new Intent(MainChoreActivity.this, OpenQuestionnaireActivity.class));
                        } else {
                            startActivity(new Intent(MainChoreActivity.this, GoodByeActivity.class));
                        }
                    }

                    @Override
                    public void onSoundClick() {
                        soundPressNum++;
                    }
                });
    }
}
