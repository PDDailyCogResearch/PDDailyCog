package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.TrialChoreActivity;

public class TrialInstrcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_instrc);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.mainButtonOk)
    public void onViewClicked() {
        startActivity(new Intent(this, TrialChoreActivity.class));
    }
}
