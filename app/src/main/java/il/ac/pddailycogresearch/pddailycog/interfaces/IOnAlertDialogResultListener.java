package il.ac.pddailycogresearch.pddailycog.interfaces;

import android.widget.Button;

/**
 * Created by User on 15/02/2018.
 */

public interface IOnAlertDialogResultListener {
    /**
     * Created by User on 17/01/2018.
     */

    interface IOnAlertDialogBooleanResultListener {
        void onResult(boolean result);
    }
    interface IOnAlertDialogIntegerResultListener {
        void onResult(int result);
    }

    interface IOnAlertDialogWithSoundResultListener {
        void onResult(boolean result);
        void onSoundClick();

    }
}
