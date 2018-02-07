package il.ac.pddailycogresearch.pddailycog.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.adapters.ViewPagerAdapter;
import il.ac.pddailycogresearch.pddailycog.customviews.NonSwipeableViewPager;
import il.ac.pddailycogresearch.pddailycog.fragments.RadioQuestionFragment;

public class DrinkChoreActivity extends AppCompatActivity implements
        RadioQuestionFragment.OnFragmentInteractionListener {

    @BindView(R.id.viewPagerDrinkActivity)
    NonSwipeableViewPager viewPagerDrinkActivity;
    @BindView(R.id.buttonDrinkActivity)
    Button buttonDrinkActivity;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_chore);
        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerDrinkActivity.setAdapter(adapter);
    }

    @OnClick({R.id.buttonDrinkActivity,R.id.buttonDrinkActivity2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonDrinkActivity:
                viewPagerDrinkActivity.setCurrentItem(viewPagerDrinkActivity.getCurrentItem() + 1);
                break;
            case R.id.buttonDrinkActivity2:
                viewPagerDrinkActivity.setCurrentItem(viewPagerDrinkActivity.getCurrentItem() - 1);
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
