package il.ac.pddailycogresearch.pddailycog;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

import il.ac.pddailycogresearch.pddailycog.receivers.ConnectivityChangeReceiver;
import il.ac.pddailycogresearch.pddailycog.receivers.DataSyncJobService;

/**
 * Created by User on 21/06/2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new ConnectivityChangeReceiver(), intentFilter);
    }

}
