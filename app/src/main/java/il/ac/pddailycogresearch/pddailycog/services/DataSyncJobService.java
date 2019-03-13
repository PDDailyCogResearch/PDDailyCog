package il.ac.pddailycogresearch.pddailycog.services;

/**
 * Created by shna0 on 14/10/2018.
 */

import android.os.Bundle;
import android.util.Log;

import com.bugfender.sdk.Bugfender;
import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseResaveImage;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnSuccessListener;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

public class DataSyncJobService extends JobService {
    private static final String TAG = DataSyncJobService.class.getSimpleName();

    private class SavingParams {
        public SavingParams(String collection, int number, String key) {
            this.collection = collection;
            this.number = number;
            this.key = key;
        }

        String collection;
        int number;
        String key;
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    List<SavingParams> imgKeysToSave;
    int successCounter;
    @Override
    public boolean onStartJob(final JobParameters job) {
        // Do some work here
        Log.d(TAG, "onStartJob");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(FirebaseIO.getInstance().isUserLogged()){

            mFirebaseAnalytics.setUserId(FirebaseIO.getInstance().getUsername());
            logEvent("onStartDataSyncJob",job.getTag());
            initImgKeys();
            for (final SavingParams imgkey : imgKeysToSave) {
                FirebaseIO.getInstance().resaveImageByKey(
                        imgkey.collection, imgkey.number, imgkey.key,
                        new IOnFirebaseResaveImage() {
                            @Override
                            public void onSuccess() {
                                successCounter++;
                                if(successCounter==imgKeysToSave.size()){
                                    jobFinished(job,false);
                                }
                            }

                            @Override
                            public void onLogEvent(String message) {
                                logEvent("resave_image",message);
                            }
                        }
                );
            }
        }
        return true; // Answers the question: "Is there still work going on?"
    }

    private void logEvent(String event, String message) {
        Bundle params = new Bundle();
        message = FirebaseIO.getInstance().getUsername() + ": "+message;
        params.putString("message",message);
        mFirebaseAnalytics.logEvent(event, params);
        Bugfender.sendIssue(event,message);
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
    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "onStopJob");
        logEvent("onStopDataSyncJob",job.getTag());
        return true; // Answers the question: "Should this job be retried?"
    }
}