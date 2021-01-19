package mc.apps.demo0.libs;

import android.Manifest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import mc.apps.demo0.R;

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
    public static final String DATE_FORMAT_FR = "dd-MM-yyyy";
    public static final String DATE_FORMAT_EN = "yyyy-MM-dd hh:mm:ss";

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
