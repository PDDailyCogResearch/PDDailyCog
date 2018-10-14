package il.ac.pddailycogresearch.pddailycog.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 17/01/2018.
 */

public final class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    private CommonUtils() {

    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showMessage(Context context, @StringRes int msgId) {
        showMessage(context, context.getResources().getString(msgId));
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getTimeStamp() {

        return new SimpleDateFormat(Consts.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static Date imageNameToDate(String imageName) {
        int tsStart = imageName.indexOf("JPEG") + 5;
        int tsEnd = tsStart + Consts.TIMESTAMP_FORMAT.length();
        String ts = imageName.substring(tsStart, tsEnd);
        DateFormat format = new SimpleDateFormat(Consts.TIMESTAMP_FORMAT, Locale.US);
        try {
            Date date = format.parse(ts);
            System.out.println("date :" + date);
            return date;
        } catch (Exception e) {
            onGeneralError(e, TAG);
        }
        return null;
    }

    public static void closeApp(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            activity.finishAffinity();
        else
            activity.finish();//TODO ask Tal
    }

    public static boolean isAirplaneMode(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        } else {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;

        }
    }

    public static void onGeneralError(Exception e, String tag) {
        Log.e(tag, e.getMessage(), e);
        Crashlytics.log(Log.ERROR, tag, e.getMessage());
        // Crashlytics.logException(e);
    }

   /* public static int[] getIdArrayFromResources(Activity activity,@ArrayRes int arrayId) {
        int[] idsArray;
        TypedArray ta= activity.getResources().obtainTypedArray(arrayId);
        int n = ta.length();
        idsArray = new int[n];
        for (int i = 0; i < n; ++i) {
            int id = ta.getResourceId(i, 0);
            if (id > 0) {
                idsArray[i] = id;
            } else {
                Log.e(TAG,"array xml error");
            }
        }
        return idsArray;
    }*/

}
