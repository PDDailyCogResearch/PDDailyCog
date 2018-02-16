package il.ac.pddailycogresearch.pddailycog.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;

/**
 * Created by User on 21/01/2018.
 */

public class DialogUtils {
    private static ProgressDialog mProgressDialog;

    private DialogUtils() {

    }

    private static ProgressDialog createLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static void showLoading(Context context) {
        hideLoading();
        mProgressDialog = createLoadingDialog(context);
    }

    public static void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public static void createAlertDialog(final Context context, @StringRes final int title, @StringRes final int message,
                                         @StringRes final int positiveButton, @StringRes final int negativeButton,
                                         final IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener alertDialogResultListener) {
        createAlertDialog(context,
                context.getResources().getString(title),
                context.getResources().getString(message),
                context.getResources().getString(positiveButton),
                context.getResources().getString(negativeButton),
                alertDialogResultListener);
    }

    public static void createAlertDialog(final Context context,String title, String message,
                                         String positiveButton, String negativeButton,
                                         final IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener alertDialogResultListener) {
        final AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(true);
                            }
                        })
                .setNegativeButton(negativeButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(false);
                            }
                        })
                .create();
        ad.show();
    }

    public static void createAlertDialog(final Context context, @StringRes final int title, @StringRes final int message,
                                         @StringRes final int singleButton,
                                         final IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener alertDialogResultListener) {
        final AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(singleButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(true);
                            }
                        })
                .create();
        ad.show();
    }

    public static void createAlertDialogWithSound(final Context context, @StringRes final int title, @StringRes final int message,
                                                  @StringRes final int positiveButton,
                                                  @StringRes final int neutralButton,
                                                  @StringRes final int negativeButton,
                                                  final IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener alertDialogResultListener) {
        createAlertDialogWithSound(context,
                context.getResources().getString(title),
                context.getResources().getString(message),
                context.getResources().getString(positiveButton),
                context.getResources().getString(neutralButton),
                context.getResources().getString(negativeButton),
                alertDialogResultListener
        );
    }

    public static void createAlertDialogWithSound(final Context context, @StringRes final int title, String message,
                                                  @StringRes final int positiveButton,
                                                  @StringRes final int neutralButton,
                                                  @StringRes final int negativeButton,
                                                  final IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener alertDialogResultListener) {
        createAlertDialogWithSound(context,
                context.getResources().getString(title),
                message,
                context.getResources().getString(positiveButton),
                context.getResources().getString(neutralButton),
                context.getResources().getString(negativeButton),
                alertDialogResultListener
        );
    }

    public static void createAlertDialogWithSound(final Context context, final String title, final String message,
                                                  final String positiveButton,
                                                  final String neutralButton,
                                                  final String negativeButton,
                                                  final IOnAlertDialogResultListener.IOnAlertDialogWithSoundResultListener alertDialogResultListener) {
        final AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(1, null);
                            }
                        })
                .setNeutralButton(neutralButton, null /*new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogResultListener.onResult(0);
                    }
                }*/)
                .setNegativeButton(negativeButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(-1, null);
                            }
                        })
                .create();
        ad.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                final Button b = ad.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        alertDialogResultListener.onResult(0, b);
                    }
                });
            }
        });

        ad.show();
    }

    public static void createTurnOffAirplaneModeAlertDialog(final Activity activity) {
        createAlertDialog(activity, R.string.reminder, R.string.turn_off_airplane_mode_alert_msg,
                R.string.open_settings, R.string.exit,
                new IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            activity.startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                        }
                        CommonUtils.closeApp(activity);
                    }
                });
    }

    public static void createTurnOnAirPlaneModeDialog(final Activity activity) {
        createAlertDialog(activity, R.string.reminder, R.string.airplane_mode_request, android.R.string.ok,
                new IOnAlertDialogResultListener.IOnAlertDialogBooleanResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        activity.startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                    }
                });
    }

}
