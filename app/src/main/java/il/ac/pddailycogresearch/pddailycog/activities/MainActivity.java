package il.ac.pddailycogresearch.pddailycog.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.AirplaneModeRequestActivity;
import il.ac.pddailycogresearch.pddailycog.model.JsonRadioButton;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.ImageUtils;
import il.ac.pddailycogresearch.pddailycog.utils.ReadJsonUtil;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainButtonOk)
    Button mainButtonOk;

    @TargetApi(26)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

 //       this.getFilesDir();
 //       File file= new File("/src/main/java/resources/question-1");
//        String appPath =getApplicationInfo().dataDir+"/files";
//        Path path = Paths.get(appPath+"/question-1");
        JsonRadioButton radioButton= ReadJsonUtil.readJsonFile("src/main/java/resources/question-1");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!FirebaseIO.getInstance().isUserLogged())
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @OnClick({R.id.mainButtonOk,R.id.buttonMainOpenQuestionnaire})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mainButtonOk:
                //TODO fix before sending
               // startActivity(new Intent(MainActivity.this, AirplaneModeRequestActivity.class));
                startActivity(new Intent(MainActivity.this, DrinkChoreActivity.class));
                break;
            case R.id.buttonMainOpenQuestionnaire:
                startActivity(new Intent(MainActivity.this, QuestionnaireActivity.class));
                break;
        }
    }
}
