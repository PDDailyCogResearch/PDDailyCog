package il.ac.pddailycogresearch.pddailycog.activities;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.google.firebase.database.DatabaseException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.TrialNoticeActivity;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseQuestionnaireListener;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

public class AirplaneModeRequestActivity extends AppCompatActivity {

    private static final String TAG = AirplaneModeRequestActivity.class.getSimpleName();

    private int nextChoreNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_airplane_mode_request);
        ButterKnife.bind(this);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!FirebaseIO.getInstance().isUserLogged())
//            startActivity(new Intent(AirplaneModeRequestActivity.this, LoginActivity.class));
//    }

    @OnClick({R.id.buttonOpenAirplaneModeSettings, R.id.buttonAirplaneOk, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonOpenAirplaneModeSettings:
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                break;
            case R.id.buttonAirplaneOk:
                if (CommonUtils.isAirplaneMode(this)) //TODO uncomment but its annoying
                    retreiveNextChoreNum();
                else
                    CommonUtils.showMessage(this, R.string.error_not_in_airplane_mode);
               // startActivity(new Intent(this, DrinkChoreActivity.class));
                break;
            case R.id.logout:
                FirebaseIO.getInstance().logout();
                CommonUtils.closeApp(this);
                break;
        }
    }


    private void retreiveNextChoreNum() {
        FirebaseIO.getInstance().retrieveLastChoreNum(
                new IOnFirebaseKeyValueListeners.OnIntValueListener() {
                    @Override
                    public void onValueRetrieved(Integer value) {
                        if (value < 0) {
                            nextChoreNum = 1;
                            chooseNextActivity();
                        } else {
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
                                            CommonUtils.onGeneralError(e, TAG);
                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        CommonUtils.onGeneralError(e, TAG);
                    }
                }
        );
    }

    private void chooseNextActivity6() {
        FirebaseIO.getInstance().retrieveQuestionnaire(new IOnFirebaseQuestionnaireListener() {
            @Override
            public void onAnswersRetreived(List<Integer> answers) {
                if(answers.size()>0){
                    startActivity(new Intent(AirplaneModeRequestActivity.this, QuestionnaireActivity.class));
                } else {
                    //chooseNextChore();
                }
            }

            @Override
            public void onError(DatabaseException e) {
                CommonUtils.onGeneralError(e,TAG);
            }
        });

    }

    private void chooseNextActivity() {
        Intent nextActivity = null;
        switch (nextChoreNum) {
            case 1:
                nextActivity = new Intent(AirplaneModeRequestActivity.this,
                        TrialNoticeActivity.class);
                break;
            case 2:
                nextActivity = new Intent(AirplaneModeRequestActivity.this,
                        DrinkInstrcActivity.class);
                //nextActivity.putExtra("")
                break;
            default:
               // CommonUtils.showMessage(this, R.string.error_no_more_chores);
                nextActivity=new Intent(AirplaneModeRequestActivity.this, QuestionnaireActivity.class);
        }
        if (nextActivity != null) {
            startActivity(nextActivity);
        }
    }


}
