package mc.apps.demo0;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mc.apps.demo0.dao.GpsDao;
import mc.apps.demo0.libs.GPSTracker;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.ClientIntervention;
import mc.apps.demo0.model.GpsPosition;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.ui.technician.TechnicianFragment;
import mc.apps.demo0.ui.technician.TechnicianFragments;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechnicianActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private static final int RESULT_LOAD_IMAGE = 2608 ;
    private static final int TECH_INTERV_CODE = 1000;
    private static final String TAG = "tests";
    private static final int CLIENT_INTERV_CODE = 2000;
    private static final long GPS_REFRESH_MILLIS = 20000 ; // 20 sec.
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technician_activity);

        if (savedInstanceState == null) {
            defineFragment(TechnicianFragment.newInstance());
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tech_toolbar_layout);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getNum().observe(
                this,
                num -> {
                    defineFragment(TechnicianFragments.newInstance(num));
                }
        );

        checkPermissions();
        //getCurrentLocation();

        doPeriodicWork();
    }

    private void checkPermissions() {
        // gradle : +  implementation "androidx.activity:activity:1.2.0-alpha04" //androidX
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permission is granted!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "Permission not granted : feature is unavailable!", Toast.LENGTH_SHORT).show();
                    }
                });
        MyTools.CheckThenAskPermissions(TechnicianActivity.this, requestPermissionLauncher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = MyTools.GetUserInSession();
        ((TextView)findViewById(R.id.title)).setText(""+user);
    }


    private void defineFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.container, fragment).commitNow();
    }

    @Override
    public void onBackPressed() {
        defineFragment(TechnicianFragment.newInstance());
    }

    public void setContent(View view){
      int id= view.getId();
        switch(id){
            case R.id.tech_btn_interv:
                defineFragment(TechnicianFragments.newInstance(0));
                break;
            case R.id.tech_btn_rapport:
                defineFragment(TechnicianFragments.newInstance(1));
                break;
            case R.id.tech_btn_histo:
                defineFragment(TechnicianFragments.newInstance(2));
                break;
        }
    }
    public void list_photos_click(View view){
        //Sélectionner images dans Galery!
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            mainViewModel.addImage(selectedImage);
        }

        if (requestCode == TECH_INTERV_CODE && resultCode == RESULT_OK) {
            mainViewModel.setRefresh(true);
            if(null != data)
                defineFragment(TechnicianFragments.newInstance(1, (Intervention) data.getSerializableExtra("interv")));
        }

        if (requestCode == CLIENT_INTERV_CODE && resultCode == RESULT_OK && null != data) {
            Intervention item = (Intervention) data.getSerializableExtra("item_filter");
            mainViewModel.setIntervention(item);
        }
    }

    /**
     * Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);

/*        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //mainViewModel.setSearch(newText);
                return true;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.appSignOut){
            MyTools.confirmLogout(this);
        }
        return true;
    }
    public void ClientIntervFilter(View view){
        Intent intent = new Intent(this, InterventionsFilterActivity.class);
        startActivityForResult(intent, CLIENT_INTERV_CODE);
    }

    /**
     * Gestion calendrier
     */
    EditText edtDateTime;
    private int mYear, mMonth, mDay, mHour, mMinute;


    public void selectCalendarDate(View view) {
        this.fulldate=false;
        SelectDate(view);
    }
    public void selectCalendarDateTime(View view) {
        this.fulldate=true;
        SelectDate(view);
    }

    private boolean fulldate=false;
    private void SelectDate(View view) {
        edtDateTime =  (EditText) view;;
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    int lastSelectedHour=0, lastSelectedMinute=0;
    boolean is24HView = true;
    public void selectCalendarTime(View view) {
        edtDateTime =  (EditText) view;;
        TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
            edtDateTime.setText(hourOfDay + ":" + minute );
            lastSelectedHour = hourOfDay;
            lastSelectedMinute = minute;
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, lastSelectedHour, lastSelectedMinute, is24HView);
        timePickerDialog.show();
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mYear = year;
        mDay = dayOfMonth;
        mMonth = month;
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);

        if(fulldate) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, mHour, mMinute, true);
            timePickerDialog.show();
        }else{
            edtDateTime.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
        }
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        edtDateTime.setText(mDay + "-" + (mMonth + 1) + "-" + mYear+" "+mHour+":"+mMinute);
    }

    /**
     * GPS Location Save
     */
    private void doPeriodicWork() {
        handler = new Handler();
        handler.post(runnableCode);
    }
    Handler handler;
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Get Current Location..");
            getCurrentLocation();
            handler.postDelayed(runnableCode, GPS_REFRESH_MILLIS);
        }
    };
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnableCode);
        Log.d(TAG, "Get Current Location END!");
        super.onDestroy();
    }
    GPSTracker gps;
    private void getCurrentLocation() {
        gps = new GPSTracker(this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //((TextView)root.findViewById(R.id.textLocation)).setText(latitude+"x"+longitude);
            saveGps(latitude, longitude);
        } else {
            gps.showSettingsAlert();
        }
    }
    GpsPosition gp;
    private void saveGps(double latitude, double longitude) {
        GpsDao dao = new GpsDao();
        User user = MyTools.GetUserInSession();

        dao.find(user.getCode(), (items, message) -> {
            List<GpsPosition> positions_ = dao.Deserialize(items, GpsPosition.class);
            if(positions_.size()>0){
                gp = positions_.get(0);
                gp.setLatitude(latitude);
                gp.setLongitude(longitude);

                dao.update(gp, (items_, message_) -> {
                    Log.i(TAG, "gps updated : "+user.getCode()+" => "+latitude+"x"+longitude);
                });
            }else{
                gp =  new GpsPosition(0,latitude, longitude, user.getCode());
                dao.add(gp, (items_, message_) -> {
                    Log.i(TAG, "gps added : "+user.getCode()+" => "+latitude+"x"+longitude);
                });
            }
        });
    }
}