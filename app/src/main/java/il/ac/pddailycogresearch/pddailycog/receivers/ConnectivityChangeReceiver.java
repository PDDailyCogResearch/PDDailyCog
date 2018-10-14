package il.ac.pddailycogresearch.pddailycog.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//import com.crashlytics.android.Crashlytics;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

public class ConnectivityChangeReceiver extends BroadcastReceiver { //ask Tal if too much for users to call this every time...
    private static final String TAG = ConnectivityChangeReceiver.class.getSimpleName();

   /* private class SavingParams {
        public SavingParams(String collection, int number, String key) {
            this.collection = collection;
            this.number = number;
            this.key = key;
        }

        String collection;
        int number;
        String key;
    }

    List<SavingParams> imgKeysToSave;

    *//***
     * allow checking wither this is the first receiver
     *//*
    private static Long firstTimestamp = Long.MIN_VALUE;
    */

    /***
     * interval to allow saving again
     *//*
    private static final long TIME_INTERVAL = 30000;


    @Override
    public void onReceive(Context context, Intent intent) {
        //act only if there is internet connection and if it is the first instance to react
        //needed because in airplane toggle happens few connectivity changes

        if (isNetworkAvailable(context) && FirebaseIO.getInstance().isUserLogged()) {
            if (firstTimestamp == Long.MIN_VALUE || System.currentTimeMillis() - firstTimestamp > TIME_INTERVAL) {
                firstTimestamp = System.currentTimeMillis();
                initImgKeys();
                for (SavingParams imgkey : imgKeysToSave) {
                    FirebaseIO.getInstance().resaveImageByKey(
                            imgkey.collection, imgkey.number, imgkey.key,
                            new IOnSuccessListener() {
                                @Override
                                public void onSuccess() {
                                    //TODO adapt or delete this sulotion, moved to JObDispatcher
                                }
                            }
                    );
                }
                Crashlytics.log(Log.ERROR, TAG, "firstTimestamp: " + firstTimestamp);
                Crashlytics.log(Log.ERROR, TAG, "System.currentTimeMillis(): " + System.currentTimeMillis());
                Crashlytics.logException(new Throwable("Receiver non-fatal: after upload"));
            } else {

                Crashlytics.log(Log.ERROR, TAG, "firstTimestamp: " + firstTimestamp);
                Crashlytics.log(Log.ERROR, TAG, "System.currentTimeMillis(): " + System.currentTimeMillis());
                Crashlytics.logException(new Throwable("Receiver non-fatal: else interval logic"));
            }
        } else {
            Crashlytics.log(Log.ERROR, TAG, "isNetworkAvailable(context): " + isNetworkAvailable(context));
            Crashlytics.logException(new Throwable("Receiver non-fatal: else connectivity"));
        }
    }


    private void initImgKeys() {
        imgKeysToSave = new ArrayList<>();
        imgKeysToSave.add(new SavingParams(Consts.CHORES_KEY, 1, Consts.RESULT_KEY_PREFIX + 1));

        imgKeysToSave.add(new SavingParams(Consts.CHORES_KEY, 2, Consts.RESULT_KEY_PREFIX + 8));
        imgKeysToSave.add(new SavingParams(Consts.CHORES_KEY, 2, Consts.RESULT_KEY_PREFIX + 11));
        imgKeysToSave.add(new SavingParams(Consts.CHORES_KEY, 3, Consts.RESULT_KEY_PREFIX + 8));
        imgKeysToSave.add(new SavingParams(Consts.CHORES_KEY, 3, Consts.RESULT_KEY_PREFIX + 11));
        imgKeysToSave.add(new SavingParams(Consts.CHORES_KEY, 4, Consts.RESULT_KEY_PREFIX + 8));
        imgKeysToSave.add(new SavingParams(Consts.CHORES_KEY, 4, Consts.RESULT_KEY_PREFIX + 11));
    }
*/
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isNetworkAvailable(context)) {
            dispatcherSchedule(context);
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
