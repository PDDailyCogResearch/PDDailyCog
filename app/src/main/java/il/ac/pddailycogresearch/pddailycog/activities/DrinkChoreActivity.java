package il.ac.pddailycogresearch.pddailycog.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.adapters.ViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.customviews.NonSwipeableViewPager;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.MediaUtils;

public class DrinkChoreActivity extends AppCompatActivity implements
        RadioQuestionFragment.OnFragmentInteractionListener {

    private static final int CHORE_NUM = 0;
    private static final String BACK_PRESS_NUM_TAG = "back_press_num";

    @BindView(R.id.viewPagerDrinkActivity)
    NonSwipeableViewPager viewPagerDrinkActivity;
    @BindView(R.id.buttonNextDrinkActivity)
    Button buttonNextDrinkActivity;
    @BindView(R.id.buttonSoundDrinkActivity)
    FloatingActionButton buttonSoundDrinkActivity;

    private ViewPagerAdapter adapter;
    private MediaPlayer mpori;
    private int backPressNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_chore);
        ButterKnife.bind(this);

        if(savedInstanceState!=null){
            backPressNum = savedInstanceState.getInt(BACK_PRESS_NUM_TAG);
        } else {
            backPressNum=0;
        }
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM);
        viewPagerDrinkActivity.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BACK_PRESS_NUM_TAG,backPressNum);
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.buttonNextDrinkActivity, R.id.buttonSoundDrinkActivity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonNextDrinkActivity:
                MediaUtils.stopMediaPlayer(buttonSoundDrinkActivity);
                buttonNextDrinkActivity.setEnabled(false);
                viewPagerDrinkActivity.setCurrentItem(viewPagerDrinkActivity.getCurrentItem() + 1);
                break;
            case R.id.buttonSoundDrinkActivity:
                int soundId = getResources().getIdentifier(
                        "cog_drink_" + String.valueOf(viewPagerDrinkActivity.getCurrentItem() + 2),//TODO adjust better
                        "raw", getPackageName());//TODO cut the files and correct the texts
                MediaUtils.toggleMediaPlayer(getApplicationContext(), soundId, buttonSoundDrinkActivity);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        MediaUtils.stopMediaPlayer(buttonSoundDrinkActivity);
        int prevItem = viewPagerDrinkActivity.getCurrentItem() - 1;
        if (prevItem < 0) {
            super.onBackPressed();
        } else {
            backPressNum++;
            viewPagerDrinkActivity.setCurrentItem(prevItem);
        }
    }

    @Override
    protected void onStop() {
        saveToDb();
        super.onStop();
    }

    private void saveToDb() {
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY,CHORE_NUM,"backPressNum",backPressNum);
    }

    //region fragment callbacks
    @Override
    public void enableNext() {
        if (!buttonNextDrinkActivity.isEnabled()) {
            buttonNextDrinkActivity.setEnabled(true);
        }
    }
    //endregion
}
