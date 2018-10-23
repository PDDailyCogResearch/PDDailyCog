package il.ac.pddailycogresearch.pddailycog.services;

/**
 * Created by shna0 on 14/10/2018.
 */

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;

import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
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

    List<SavingParams> imgKeysToSave;
    int successCounter;
    @Override
    public boolean onStartJob(final JobParameters job) {
        // Do some work here
        Log.d(TAG, "onStartJob");
        Crashlytics.logException(new Throwable("onStartJob"));
        if(FirebaseIO.getInstance().isUserLogged()){
            initImgKeys();
            for (SavingParams imgkey : imgKeysToSave) {
                FirebaseIO.getInstance().resaveImageByKey(
                        imgkey.collection, imgkey.number, imgkey.key,
                        new IOnSuccessListener() {
                            @Override
                            public void onSuccess() {
                                successCounter++;
                                if(successCounter==imgKeysToSave.size()){
                                    jobFinished(job,false);
                                }
                            }
                        }
                );
            }
        }
        return true; // Answers the question: "Is there still work going on?"
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
        Crashlytics.logException(new Throwable("onStopJob"));
        return true; // Answers the question: "Should this job be retried?"
    }
}