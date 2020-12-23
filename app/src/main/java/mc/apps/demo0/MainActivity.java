package mc.apps.demo0;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import mc.apps.demo0.libs.Tools;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorDrawable color = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark, null));
        getSupportActionBar().setBackgroundDrawable(color);

        /**
         * handle buttons
         */
        //handleButtons();
        checkPermissions();
    }

    private void handleButtons() {
        /**
         * Gérer boutons / layout
         */
        Button btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener((v)->checkPermissions());
    }
    private void checkPermissions() {
        // gradle : +  implementation "androidx.activity:activity:1.2.0-alpha04" //androidX
        ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                    Toast.makeText(this, "Permission is granted!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission not granted : feature is unavailable!", Toast.LENGTH_SHORT).show();
            });
        Tools.CheckThenAskPermissions(MainActivity.this, requestPermissionLauncher);
    }

}