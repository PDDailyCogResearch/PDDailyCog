package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.QuestionnaireActivity;

public class OpenQuestionnaireActivity extends AppCompatActivity {

    private static final String CHORE_NUM_NAME = "chore-num";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_questionnaire);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonGoodByeOK)
    public void onViewClicked() {
        Intent newChore = new Intent(this, QuestionnaireActivity.class);
        newChore.putExtra(CHORE_NUM_NAME,getIntent().getIntExtra(CHORE_NUM_NAME,2));
        startActivity(newChore);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //unable back
    }
}
