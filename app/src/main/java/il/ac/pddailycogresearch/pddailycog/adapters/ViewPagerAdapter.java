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
    private static final int COUNT = 13;//TODO
    private int activityChoreNum;

    public ViewPagerAdapter(FragmentManager fm, int activityChoreNum) {
        super(fm);
        this.activityChoreNum = activityChoreNum;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment currentFragment = null;

        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 8:
            case 9:
            case 11:
                currentFragment = RadioQuestionFragment.newInstance(position, activityChoreNum);
                break;
            case 6:
            case 12:
                currentFragment = TextFragment.newInstance(position, activityChoreNum);
                break;
            case 7:
            case 10:
                currentFragment = PhotographFragment.newInstance(position, activityChoreNum);
                break;
            case 5:
                currentFragment = TextFragment.newInstance(position, activityChoreNum);


        }
        return currentFragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
