package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.nio.file.Path;
import java.nio.file.Paths;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.DrinkChoreActivity;
import il.ac.pddailycogresearch.pddailycog.activities.DrinkInstrcActivity;
import il.ac.pddailycogresearch.pddailycog.activities.LoginActivity;
import il.ac.pddailycogresearch.pddailycog.activities.MainActivity;
import il.ac.pddailycogresearch.pddailycog.activities.TrialChoreActivity;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.model.JsonRadioButton;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.ReadJsonUtil;

public class AirplaneModeRequestActivity extends AppCompatActivity {


    private int nextChoreNum=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airplane_mode_request);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!FirebaseIO.getInstance().isUserLogged())
            startActivity(new Intent(AirplaneModeRequestActivity.this, LoginActivity.class));
    }

    @OnClick({R.id.buttonOpenAirplaneModeSettings, R.id.buttonAirplaneOk,R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonOpenAirplaneModeSettings:
               startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                break;
            case R.id.buttonAirplaneOk:
//                if(CommonUtils.isAirplaneMode(this)) //TODO uncomment but its annoying
//                    retreiveNextChoreNum();
//               else
//                   CommonUtils.showMessage(this,R.string.error_not_in_airplane_mode);
               startActivity(new Intent(this, DrinkChoreActivity.class));
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
        Intent nextActivity=null;
        switch (nextChoreNum){
            case 1:
                nextActivity = new Intent(AirplaneModeRequestActivity.this,
                        TrialInstrcActivity.class);
                break;
            case 2:
                nextActivity = new Intent(AirplaneModeRequestActivity.this,
                        DrinkInstrcActivity.class);
                break;
            default:
                CommonUtils.showMessage(this,R.string.error_no_more_chores);
        }
        if(nextActivity!=null) {
            startActivity(nextActivity);
        }

    }


}
