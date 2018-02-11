package il.ac.pddailycogresearch.pddailycog.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.PhotographFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.TextFragment;

/**
 * Created by User on 07/02/2018.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int COUNT = 4;//TODO
    private int activityChoreNum;

    public ViewPagerAdapter(FragmentManager fm, int activityChoreNum) {
        super(fm);
        this.activityChoreNum = activityChoreNum;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment currentFragment = null;

         switch (position) {
            case 1:
                currentFragment = PhotographFragment.newInstance(position, activityChoreNum);
                break;
            case 0:
                currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum);
                break;
            case 2:
                currentFragment = TextFragment.newInstance(position, activityChoreNum);
                break;
             case 3:
                 currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum);
                 break;
        }
        return currentFragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
