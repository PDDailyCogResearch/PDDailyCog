package il.ac.pddailycogresearch.pddailycog;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

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
        dispatcherSchdelue(getApplicationContext());
    }

    private void dispatcherSchdelue(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(DataSyncJobService.class) // the JobService that will be called
                .setTag(DataSyncJobService.class.getSimpleName()+"_timed")
                .setTrigger(Trigger.executionWindow((int)TimeUnit.MINUTES.toSeconds(5),(int) TimeUnit.HOURS.toSeconds(2)))
                .setReplaceCurrent(true)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);

    }
}
