package il.ac.pddailycogresearch.pddailycog.interfaces;

import android.widget.Button;

/**
 * Created by User on 15/02/2018.
 */

public class IOnAlertDialogResultListener {
    /**
     * Created by User on 17/01/2018.
     */

    public  interface IOnAlertDialogBooleanResultListener {
        void onResult(boolean result);
    }
    public  interface IOnAlertDialogIntegerResultListener {
        void onResult(int result);
    }

    public  interface IOnAlertDialogWithSoundResultListener {
        void onResult(boolean result);
        void onSoundClick();

    }
}
