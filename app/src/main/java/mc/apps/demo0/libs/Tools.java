package mc.apps.demo0.libs;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import mc.apps.demo0.R;

public class Tools {
    private static final String TAG = "demo";

    /**
     * UI
     */


    /**
     * Permissions :
     * check permissions + ask
     */
    private static String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
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
