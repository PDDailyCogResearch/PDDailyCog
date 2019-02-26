package il.ac.pddailycogresearch.pddailycog.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import il.ac.pddailycogresearch.pddailycog.BuildConfig;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.services.DataSyncJobService;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;

public class MyPackageReplacedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(
//                intent.getAction())) {
//            FirebaseIO.getInstance().saveKeyValuePair("test", 1, "version-code", BuildConfig.VERSION_CODE);
//        }
        //just triger the app class so data can be gethered

        dispatcherSchedule(context); //TODO delete in next package update...
    }

    private void dispatcherSchedule(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(DataSyncJobService.class) // the JobService that will be called
                .setTag(DataSyncJobService.class.getSimpleName())
                .setTrigger(Trigger.NOW)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);

    }
}
