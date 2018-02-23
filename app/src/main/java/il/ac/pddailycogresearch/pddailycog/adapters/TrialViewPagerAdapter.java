package il.ac.pddailycogresearch.pddailycog.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.CheckBoxFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.DragListFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.InstructionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.PhotographFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.TextFragment;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

/**
 * Created by User on 23/02/2018.
 */

public class TrialViewPagerAdapter extends ViewPagerAdapter {
    public TrialViewPagerAdapter(FragmentManager fm, int activityChoreNum) {
        super(fm, activityChoreNum);
        count = 5;
    }

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
                currentFragment = DragListFragment.newInstance(position, activityChoreNum);
                break;
            case 4:
                currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum, Consts.TRIAL_CHORE_ASSETS_PREFIX);
                break;
            default:
                currentFragment = null;
        }
        return currentFragment;
    }
}
