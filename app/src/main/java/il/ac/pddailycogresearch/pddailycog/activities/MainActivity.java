package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.TrialInstrcActivity;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.mainButtonOk)
    Button mainButtonOk;
    @BindView(R.id.buttonMainOpenQuestionnaire)
    Button buttonMainOpenQuestionnaire;

    int nextChoreNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buttonMainOpenQuestionnaire.setText("logout");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!FirebaseIO.getInstance().isUserLogged())
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @OnClick({R.id.mainButtonOk, R.id.buttonMainOpenQuestionnaire})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mainButtonOk:
                retreiveNextChoreNum();

                //TODO fix before sending
                // startActivity(new Intent(MainActivity.this, AirplaneModeRequestActivity.class));
                //startActivity(new Intent(MainActivity.this, DrinkChoreActivity.class));
                // startActivity(new Intent(MainActivity.this, DrinkInstrcActivity.class));
                // Crashlytics.getInstance().crash();
                break;
            case R.id.buttonMainOpenQuestionnaire:
                //  startActivity(new Intent(MainActivity.this, QuestionnaireActivity.class)); TODO fix before sending
                FirebaseIO.getInstance().logout();
                break;
        }
    }

    private void retreiveNextChoreNum() {
        FirebaseIO.getInstance().retrieveLastChoreNum(
                new IOnFirebaseKeyValueListeners.OnIntValueListener() {
                    @Override
                    public void onValueRetrieved(Integer value) {
                        if(value<0) {
                            nextChoreNum=1;
                            chooseNextActivity();
                        }
                        else {
                            nextChoreNum = value;
                            FirebaseIO.getInstance().retreieveBooleanValueByKey(
                                    Consts.CHORES_KEY, value, Consts.IS_COMPLETED_KEY,
                                    new IOnFirebaseKeyValueListeners.OnBooleanListValueListener() {
                                        @Override
                                        public void onValueRetrieved(Boolean value) {
                                            if (value) {
                                                nextChoreNum++;
                                            }
                                            chooseNextActivity();
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        //TODO start first activity?
                    }
                }
        );
    }

    private void chooseNextActivity() {
        switch (nextChoreNum){
            case 1:
                startActivity(new Intent(MainActivity.this, TrialInstrcActivity.class));
                break;
            case 2:
                startActivity(new Intent(MainActivity.this, DrinkInstrcActivity.class));
                break;
            default:
                CommonUtils.showMessage(this,R.string.error_no_more_chores);
        }
    }
}
