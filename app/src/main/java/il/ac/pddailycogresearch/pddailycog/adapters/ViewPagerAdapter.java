package il.ac.pddailycogresearch.pddailycog.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import il.ac.pddailycogresearch.pddailycog.fragments.RadioQuestionFragment;

/**
 * Created by User on 07/02/2018.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int COUNT = 15;
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment currentFragment = null;
        currentFragment = RadioQuestionFragment.newInstance(String.valueOf(position), "hi");
       /* switch (position) {
            case 0:
                currentFragment = RadioQuestionFragment.newInstance("0", "hi");
                break;
            case 1:
                currentFragment = RadioQuestionFragment.newInstance("1", "hi");
                break;
            case 2:
                currentFragment = RadioQuestionFragment.newInstance("2", "hi");
                break;
        }*/
        return currentFragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
