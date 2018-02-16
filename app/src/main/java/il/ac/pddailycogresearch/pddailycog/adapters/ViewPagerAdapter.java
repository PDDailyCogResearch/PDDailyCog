package il.ac.pddailycogresearch.pddailycog.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.BaseViewPagerFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.CheckBoxFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.DragListFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.PhotographFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.TextFragment;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

/**
 * Created by User on 07/02/2018.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int COUNT = 14;//TODO
    private int activityChoreNum;
    private BaseViewPagerFragment currentFragment;
    private BaseViewPagerFragment previousFragment;

    public ViewPagerAdapter(FragmentManager fm, int activityChoreNum) {
        super(fm);
        this.activityChoreNum = activityChoreNum;
    }


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
                currentFragment = TextFragment.newInstance(position, activityChoreNum, R.string.drink_first_text_instr);
                break;
            case 8:
                currentFragment = PhotographFragment.newInstance(position, activityChoreNum, R.string.dring_photo_instr);
                break;
            case 11:
                currentFragment = PhotographFragment.newInstance(position, activityChoreNum, R.string.dring_photo_dring_done);
                break;
            case 13:
                currentFragment = TextFragment.newInstance(position, activityChoreNum, R.string.drink_time_valuat_text_instrc);
                break;
            default:
                currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum, Consts.DRINK_CHORE_ASSETS_PREFIX);

        }
        return currentFragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
         previousFragment = currentFragment;
        if (currentFragment != object) {
            currentFragment = (BaseViewPagerFragment) object;
        }

        super.setPrimaryItem(container, position, object);
    }

    public BaseViewPagerFragment getCurrentFragment() {
        return currentFragment;
    }

    public BaseViewPagerFragment getPreviousFragment() {
        return previousFragment;
    }
}
