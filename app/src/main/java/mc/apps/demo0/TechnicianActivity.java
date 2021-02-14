package mc.apps.demo0;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mc.apps.demo0.dao.GpsDao;
import mc.apps.demo0.dao.MessageDao;
import mc.apps.demo0.libs.GPSTracker;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.GpsPosition;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.Message;
import mc.apps.demo0.model.User;
import mc.apps.demo0.ui.technician.TechnicianFragment;
import mc.apps.demo0.ui.technician.TechnicianFragments;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechnicianActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private static final int RESULT_LOAD_IMAGE = 2608 ;
    private static final int TECH_INTERV_CODE = 1000;
    private static final String TAG = "tests";
    private static final int CLIENT_INTERV_CODE = 2000;
    private static final long GPS_REFRESH_MILLIS = 600000 ; // 10 min.
    private static final long MSG_REFRESH_MILLIS = 300000 ; // 5 min.
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

        createNotificationChannel();
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

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
      /*  MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
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
        else if(item.getItemId()==R.id.appMsg){
            startActivity(new Intent(this, MessagesActivity.class));
        }else if(item.getItemId()==R.id.appSettings){
            startActivity(new Intent(this, SettingsActivity.class));
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
     * Read Messages
     */
    private void doPeriodicWork() {
        handler1 = new Handler();
        handler1.post(gpsRunnableCode);

        getMessagesTask();
    }

    Handler handler1;
    private Runnable gpsRunnableCode = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Get Current Location..");
            getCurrentLocation();
            handler1.postDelayed(gpsRunnableCode, GPS_REFRESH_MILLIS);
        }
    };

    Timer msgTimer;
    private void getMessagesTask(){
        msgTimer = new Timer();
        msgTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "run: time task.."+MyTools.getCurrentTime());
                getUnseenMessages();
            }

        },  0, MSG_REFRESH_MILLIS);

    }
    private void getUnseenMessages() {
        MessageDao dao = new MessageDao();
        dao.find(MyTools.GetUserInSession().getCode(), (items, message)->{
            Log.i(TAG, "getUnseenMessages: "+items.size());
            if(items.size()>0) {
                List<Message> messages = dao.Deserialize(items, Message.class);
                sendNotification(messages);
            }
        });
    }
   /* private void addNotification(List<Message> msgs) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_send_msg)
                        .setContentTitle("Message!")
                        .setContentText("Vous avez "+msgs.size()+" message"+(msgs.size()>1?"s":"")+"!");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
*/
    @Override
    protected void onDestroy() {
        handler1.removeCallbacks(gpsRunnableCode);
        Log.i(TAG, "Gps Tracker: Stop");
        msgTimer.cancel();
        Log.i(TAG, "MessageTimer: Cancel");

        deleteLocation();
        super.onDestroy();
    }
    private void deleteLocation() {
        GpsDao dao = new GpsDao();
        dao.delete(MyTools.GetUserInSession().getCode(), (i,m)->{});
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


    /**
     * Notifications!
     */

    private static final String ACTION_UPDATE_NOTIFICATION = "mc.apps.demo0.ACTION_UPDATE_NOTIFICATION";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private NotificationReceiver mReceiver = new NotificationReceiver();

    public void sendNotification(List<Message> messages) {
/*
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
*/

        StringBuilder sb=  new StringBuilder();
        for (Message message : messages){
            sb.append(message.getFromUser()+" : "+message.getMessage()+"\n");
        }
        Log.i(TAG, "sendNotification: "+sb.toString());
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder("Nouveau(x) Message(s)", sb.toString());
        //notifyBuilder.addAction(R.drawable.ic_send_msg, "Read", updatePendingIntent);

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    public void updateNotification() {
/*        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_logo_red);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder("Message(s)!","lorem ipsum bla bla!!");

        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("updated.."));

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());*/

        // Disable the update button, leaving only the cancel button enabled.
        // setNotificationButtonState(false, false, true);
    }
    private NotificationCompat.Builder getNotificationBuilder(String title, String notification_text) {
        Intent notificationIntent = new Intent(this, MessagesActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(notification_text)
                .setSmallIcon(R.drawable.ic_send_msg)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }
    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "notification_channel_name",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            notificationChannel.setDescription("notification_channel_description");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
    public class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }

}