package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.AirplaneModeRequestActivity;
import il.ac.pddailycogresearch.pddailycog.activities.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {


    @BindView(R.id.TextviewPrivacyPolicy)
    TextView TextviewPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        TextviewPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
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
        //startActivity(new Intent(WelcomeActivity.this, ListChoreActivity.class));
    }
}
