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
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.ui.main.SectionsPagerAdapter;
import mc.apps.demo0.viewmodels.MainViewModel;

public class SupervisorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "tests";
    private static final int SELECT_REQUEST_CODE = 2608;
    private static final int REQUEST_FILTRE_CODE = 1603;


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

        //current user
        User user = getCurrentUser();
        ((TextView)findViewById(R.id.title)).setText(""+user);

        //list interventions
        refreshListAsync();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getFilter().observe(
                this,
                filter -> {
                    if (filter.isEmpty())
                        refreshListAsync();
                    else
                    if(adapter!=null)
                        adapter.setFilter(filter);
                }
        );
    }
    MainViewModel mainViewModel;

    private User getCurrentUser() {
        User user = (User) getIntent().getSerializableExtra("user");
        return user;
    }

    @Override
    public void onBackPressed() {
        MyTools.confirmExit(this);
        return;
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

                if(newText == null || newText.length() == 0)
                    refreshListAsync();
                else
                    adapter.getFilter().filter(newText);

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.appSignOut){
            MyTools.confirmExit(this);
        }
        return true;
    }

    /**
     * Gestion liste Interventions
     */
    List<Intervention> items = new ArrayList<Intervention>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    InterventionsAdapter adapter;
    private void loadList(){
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InterventionsAdapter(
                items,
                (position, item) -> {
                    Intent intent = new Intent(SupervisorActivity.this, InterventionActivity.class);
                    intent.putExtra("intervention", (Intervention)item);
                    startActivity(intent);
                }
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                SupervisorActivity.this, R.anim.layout_fall_down_animation
        ));

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshListAsync();
            }
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright
        );
    }
    private void refreshListAsync() {

        InterventionDao dao = new InterventionDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, Intervention.class);
            Log.i(TAG, "items : "+items);

            //order by date début
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream().sorted((o1, o2)->o1.getDateDebutPrevue().compareTo(o2.getDateDebutPrevue()))
                        .collect(Collectors.toList());

                Log.i(TAG, "refreshListAsync: ordered?");
            }

            loadList();
            swipeContainer.setRefreshing(false);
            //runLayoutAnimation(recyclerView);
        });
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


    public void list_techs_click(View view){
        mainViewModel.clearSelected();
        startActivityForResult(new Intent(this, SelectActivity.class), SELECT_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, requestCode+" : "+data, Toast.LENGTH_SHORT).show();
        if(requestCode==SELECT_REQUEST_CODE){
            if(data.getSerializableExtra("data")!=null) {
                List<User> selected = (List<User>) data.getSerializableExtra("data");
                for (User u : selected)
                    mainViewModel.updateSelected(u, true);
            }
        }
        if(requestCode==REQUEST_FILTRE_CODE){

            String codeClient = data.getStringExtra("codeClient");
            String codeSupervisor = data.getStringExtra("codeSupervisor");
            String dateDebutPrev = data.getStringExtra("dateDebutPrev");
            String dateDebutReel = data.getStringExtra("dateDebutReel");
            int status = data.getIntExtra("status",0);

            Log.i(TAG, "onActivityResult: "+codeClient+" "+dateDebutPrev+" "+status);

            Hashtable<String, Object> filter = new Hashtable();
            filter.put("codeClient", codeClient);
            filter.put("codeSupervisor", codeSupervisor);
            filter.put("dateDebutPrev", dateDebutPrev);
            filter.put("dateDebutReel", dateDebutReel);
            filter.put("status", status);

            mainViewModel.setFilter(filter);
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

}