package mc.apps.demo0.libs;

import android.Manifest;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyTools {
    private static final String TAG = "demo";

    /**
     * UI
     */
    public static void confirmExit(Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("Vous êtes sûr de vouloir quitter?");

        alertDialogBuilder.setPositiveButton("OUI", (dialog, which) -> activity.finish());
        alertDialogBuilder.setNegativeButton("NON", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * Date + Time
     */

    public static final String DATE_FORMAT_12 = "hh:mm";
    public static final String DATE_FORMAT_24 = "HH:mm";

    public static final String DATE_FORMAT_EN_SHORT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_EN = "yyyy-MM-dd hh:mm:ss";

    public static final String DATE_FORMAT_FR_SHORT = "dd-MM-yyyy";
    public static final String DATE_FORMAT_FR = "dd-MM-yyyy HH:mm";

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_24);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FR);
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
    public static Date getDateOfString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_EN);
        try {
            return dateFormat.parse(dateString);
        }catch(ParseException e){
            return null;
        }
    }
    public static Date getDateFrOfString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FR_SHORT);
        try {
            return dateFormat.parse(dateString);
        }catch(ParseException e){
            return null;
        }
    }
    public static String formatDateNotTimeFr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FR_SHORT);
        return dateFormat.format(date);
    }

    public static String formatDateFr(String dateString) {
        Date date = getDateOfString(dateString);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FR);
        return dateFormat.format(date);
    }
    public static String formatTimeFr(String dateString) {
        Date date = getDateOfString(dateString);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_24);
        return dateFormat.format(date);
    }
    public static boolean DateEquals(String date1, String date2){
        try {
            return MyTools.formatDateFr(date1).split(" ")[0].replace("-0","-").equals(date2.split(" ")[0]);
        }catch(Exception e){
            Log.i(TAG, "DateEquals: "+e.getMessage());
        }
        return false;

    }


/*    public static Date getTimeOfString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_24);
        try {
            return dateFormat.parse(dateString);
        }catch(ParseException e){
            return null;
        }
    }*/

    /**
     * Permissions :
     * check permissions + ask
     */
    private static String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    public static void CheckThenAskPermissions(Context context, ActivityResultLauncher<String> requestPermissionLauncher){
        boolean result_check = true;
        for (String permission : REQUIRED_PERMISSIONS)
            result_check = result_check && (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);

        if (result_check) {
            Log.i(TAG , "CheckPermissions: performAction"); //performAction(...);
        }
        else {
            Log.i(TAG , "CheckPermissions:  requestPermissionLauncher.launch");
            // ask for the permission(s).
            for (String permission : REQUIRED_PERMISSIONS)
                requestPermissionLauncher.launch(permission);
        }
    }


}
