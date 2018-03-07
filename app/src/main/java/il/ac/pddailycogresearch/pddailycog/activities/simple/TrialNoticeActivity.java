package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;

public class TrialNoticeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_notice);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.mainButtonOk)
    public void onViewClicked() {
        startActivity(new Intent(TrialNoticeActivity.this,TrialInstrcActivity.class));
    }
}
