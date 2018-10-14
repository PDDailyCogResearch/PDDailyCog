package il.ac.pddailycogresearch.pddailycog;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import il.ac.pddailycogresearch.pddailycog.receivers.ConnectivityChangeReceiver;

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
