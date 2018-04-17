package il.ac.pddailycogresearch.pddailycog.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.BaseViewPagerFragment;

/**
 * Created by User on 07/02/2018.
 */

public abstract class BaseViewPagerAdapter extends FragmentStatePagerAdapter {
    protected int activityChoreNum;
    private BaseViewPagerFragment currentFragment;
    private BaseViewPagerFragment previousFragment;

    public BaseViewPagerAdapter(FragmentManager fm, int activityChoreNum) {
        super(fm);
        this.activityChoreNum = activityChoreNum;
    }


    @Override
    public abstract Fragment getItem(int position);

    @Override
    public abstract int getCount();

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
