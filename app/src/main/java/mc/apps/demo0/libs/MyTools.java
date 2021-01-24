package mc.apps.demo0.libs;

import android.Manifest;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mc.apps.demo0.StartActivity;
import mc.apps.demo0.model.User;

public class MyTools {
    private static final String TAG = "demo";

    /**
     * Session (Shared Preferences)
     */
/*    public static final String MyPREFERENCES = "Session" ;
    public static final String CURRENT_USER_NAME = "user_name";
    private static final String CURRENT_USER_CODE = "user_code";*/

    //private static SharedPreferences sharedPreferences;

    public static User CurrentUser = null ;

    public static void InitSession(Context context) {
        //sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public static void SetUserInSession(User user){
       /* SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_USER_CODE, currentUser.getCode());
        editor.putString(CURRENT_USER_NAME, currentUser.getFirstname()+" "+currentUser.getLastname());
        editor.commit();*/
        CurrentUser = user;
    }
    public static void ClearSession(){
       /* SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();*/
        CurrentUser = null;
    }
    public static User GetUserInSession(){
        //return sharedPreferences.getString(CURRENT_USER_NAME,"");
        return CurrentUser;
    }

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
    public static void confirmLogout(Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("Vous êtes sûr de point de vous déconnecter..");
        alertDialogBuilder.setPositiveButton("Se Déconnecter",
                (dialog, which) -> {
                    activity.startActivity(new Intent(activity, StartActivity.class));
                    activity.finish();
                });
        alertDialogBuilder.setNegativeButton("Annuler", null);

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

    public static String getCurrentDateCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
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
