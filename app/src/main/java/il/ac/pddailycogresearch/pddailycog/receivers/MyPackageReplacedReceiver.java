package il.ac.pddailycogresearch.pddailycog.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import il.ac.pddailycogresearch.pddailycog.BuildConfig;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;

public class MyPackageReplacedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(
//                intent.getAction())) {
//            FirebaseIO.getInstance().saveKeyValuePair("test", 1, "version-code", BuildConfig.VERSION_CODE);
//        }
        //just triger the app class so data can be gethered

    }
}
