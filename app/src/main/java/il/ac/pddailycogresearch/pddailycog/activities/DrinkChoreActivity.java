package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
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
import il.ac.pddailycogresearch.pddailycog.activities.simple.GoodByeActivity;
import il.ac.pddailycogresearch.pddailycog.adapters.ViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.customviews.NonSwipeableViewPager;
import il.ac.pddailycogresearch.pddailycog.fragments.viewpager.RadioQuestionFragment;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.MediaUtils;

public class DrinkChoreActivity extends AppCompatActivity implements
        RadioQuestionFragment.OnFragmentInteractionListener {

    private static final int CHORE_NUM = 2;

    @BindView(R.id.viewPagerDrinkActivity)
    NonSwipeableViewPager viewPagerDrinkActivity;
    @BindView(R.id.buttonNextDrinkActivity)
    Button buttonNextDrinkActivity;
    @BindView(R.id.buttonSoundDrinkActivity)
    FloatingActionButton buttonSoundDrinkActivity;

    private ViewPagerAdapter adapter;
    private MediaPlayer mpori;
    private int backPressNum;
    private int soundPressNum;
    private long currentSessionStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_chore);
        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), CHORE_NUM);
        viewPagerDrinkActivity.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentSessionStartTime = System.currentTimeMillis();
    }

    @OnClick({R.id.buttonNextDrinkActivity, R.id.buttonSoundDrinkActivity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonNextDrinkActivity:
                MediaUtils.stopMediaPlayer(buttonSoundDrinkActivity);
                buttonNextDrinkActivity.setEnabled(false);
                int nextPage = viewPagerDrinkActivity.getCurrentItem() + 1;
                if (nextPage == adapter.getCount()) {
                    startActivity(new Intent(this, GoodByeActivity.class));
                } else {
                    viewPagerDrinkActivity.setCurrentItem(viewPagerDrinkActivity.getCurrentItem() + 1);
                }
                break;
            case R.id.buttonSoundDrinkActivity:
                int soundId = getResources().getIdentifier(
                        "cog_drink_" + String.valueOf(viewPagerDrinkActivity.getCurrentItem() + 2),//TODO adjust better
                        "raw", getPackageName());//TODO cut the files and correct the texts
                MediaUtils.toggleMediaPlayer(getApplicationContext(), soundId, buttonSoundDrinkActivity);
                if (MediaUtils.isPlaying()) {
                    soundPressNum++;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backPressNum++;
        MediaUtils.stopMediaPlayer(buttonSoundDrinkActivity);
        int prevItem = viewPagerDrinkActivity.getCurrentItem() - 1;
        if (prevItem < 0) {
            super.onBackPressed();
        } else {
            viewPagerDrinkActivity.setCurrentItem(prevItem);
        }
    }

    @Override
    protected void onStop() {
        saveToDb();
        addTimeToDb();
        super.onStop();
    }

    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, Consts.TIME_KEY_PREFIX + "total", elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    private void saveToDb() {
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "backPressNum", backPressNum);
        backPressNum = 0;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, CHORE_NUM, "soundPressNum", soundPressNum);
        soundPressNum = 0;
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
