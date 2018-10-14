package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.AirplaneModeRequestActivity;
import il.ac.pddailycogresearch.pddailycog.activities.LoginActivity;
import il.ac.pddailycogresearch.pddailycog.receivers.ConnectivityChangeReceiver;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!FirebaseIO.getInstance().isUserLogged())
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    @OnClick(R.id.BtnOk)
    public void onViewClicked() {
       startActivity(new Intent(WelcomeActivity.this, AirplaneModeRequestActivity.class));
    }
}
