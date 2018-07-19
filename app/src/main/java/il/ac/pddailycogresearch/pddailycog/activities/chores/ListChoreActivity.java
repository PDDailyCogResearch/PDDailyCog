package il.ac.pddailycogresearch.pddailycog.activities.chores;

import il.ac.pddailycogresearch.pddailycog.adapters.BaseViewPagerAdapter;

/**
 * Created by shna0 on 18/07/2018.
 */

public class ListChoreActivity extends BaseChoreActivity {
    private static final String TAG = ListChoreActivity.class.getSimpleName();
    private static final int CHORE_NUM = 3;

    @Override
    protected BaseViewPagerAdapter createViewPagerAdapter() {
        return null;
    }

    @Override
    protected int getChoreNum() {
        return CHORE_NUM;
    }

    @Override
    protected void toggleSound() {

    }

    @Override
    protected boolean moveWithDialogIfNeed(int currentPage) {
        return false;
    }
}
