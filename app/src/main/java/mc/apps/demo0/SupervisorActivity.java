package mc.apps.demo0;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.ui.main.SectionsPagerAdapter;
import mc.apps.demo0.ui.technician.TechnicianFragments;
import mc.apps.demo0.viewmodels.MainViewModel;

public class SupervisorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "tests";
    private static final int SELECT_REQUEST_CODE_1 = 1000;
    private static final int SELECT_REQUEST_CODE_2 = 2000;
    private static final int REQUEST_FILTRE_CODE = 1603;
    //private static final int CODE_CLIENT_SELECT = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar)findViewById(R.id.adminToolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getNum().observe(
                this,
                num -> {
                    //Toast.makeText(this, "num="+num, Toast.LENGTH_SHORT).show();
                    viewPager.setCurrentItem(num, true);
                }
        );
    }
    MainViewModel mainViewModel;

    @Override
    protected void onResume() {
        super.onResume();

        User user = MyTools.GetUserInSession();
        ((TextView)findViewById(R.id.title)).setText(""+user);
    }

    @Override
    public void onBackPressed() {
        MyTools.confirmLogout(this);
    }

    /**
     * Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_superv, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mainViewModel.setSearch(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.appSignOut){
            MyTools.confirmLogout(this);
        }else if(item.getItemId()==R.id.appSettings){
           startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_REQUEST_CODE_1 && data!=null){
                if(data.getSerializableExtra("data")!=null) {
                    List<User> selected = (List<User>) data.getSerializableExtra("data");
                    for (User u : selected)
                        mainViewModel.updateSelected(u, true);
                }
        }
        if(requestCode==SELECT_REQUEST_CODE_2 && data!=null){
            if(data.getSerializableExtra("data")!=null) {
                User selected = (User) data.getSerializableExtra("data");
                mainViewModel.setUser(selected);
            }
        }
        //if(requestCode==CODE_CLIENT_SELECT){
        if(data!=null)
            if(data.getSerializableExtra("data")!=null) {
                try {
                    Client selected = (Client) data.getSerializableExtra("data");
                    mainViewModel.setClient(selected);
                }catch(Exception e){ }
            }
        //}
        if(requestCode==REQUEST_FILTRE_CODE){
            if(data!=null) {
                String codeClient = data.getStringExtra("codeClient");
                String codeSupervisor = data.getStringExtra("codeSupervisor");
                String dateDebutPrev = data.getStringExtra("dateDebutPrev");
                String dateDebutReel = data.getStringExtra("dateDebutReel");
                int status = data.getIntExtra("status", 0);

                Hashtable<String, Object> filter = new Hashtable();
                filter.put("codeClient", codeClient);
                filter.put("codeSupervisor", codeSupervisor);
                filter.put("dateDebutPrev", dateDebutPrev);
                filter.put("dateDebutReel", dateDebutReel);
                filter.put("status", status);

                mainViewModel.setFilter(filter);
            }else{
                //TODO : clear filter!
            }
        }

    }

    /**
     * Filtre Détaillé Interventions (Appel)
     * @param view
     */
    public void SearchDetailInterv(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, REQUEST_FILTRE_CODE);
        overridePendingTransition(R.anim.slide_down, R.anim.slide_down);
    }

    public void list_techs_click(View view){
        mainViewModel.clearSelected();

        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra("num",1);
        startActivityForResult(intent, SELECT_REQUEST_CODE_1);
    }

    public void list_supervs_click(View view){
        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra("num",2);
        startActivityForResult(intent, SELECT_REQUEST_CODE_2);
    }

    /**
     * Gestion calendrier
     */
    EditText edtDateTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public void selectCalendarDateTime(View view) {

        edtDateTime = (EditText) view;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mYear = year;
        mDay = dayOfMonth;
        mMonth = month;
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, mHour, mMinute, true);
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        edtDateTime.setText(mDay + "-" + (mMonth + 1) + "-" + mYear+" "+mHour+":"+mMinute);
    }





}