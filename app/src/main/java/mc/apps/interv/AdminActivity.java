package mc.apps.interv;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Hashtable;
import java.util.List;

import mc.apps.interv.libs.MyTools;
import mc.apps.interv.model.User;
import mc.apps.interv.viewmodels.MainViewModel;

public class AdminActivity extends AppCompatActivity {

    private static final int REQUEST_FILTRE_CODE = 1603 ;
    private static final String TAG = "tests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                                        R.id.navigation_comptes,
                                                        R.id.navigation_clients,
                                                        R.id.navigation_interventions,
                                                        R.id.navigation_rapports
                                                ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.admin_toolbar_layout);
        setTitle("");

        Intent intent = getIntent();
        int currentFragment = intent.getIntExtra("num", 1);

        if(currentFragment==3)
            navView.setSelectedItemId(R.id.navigation_clients);
        if(currentFragment==2)
            navView.setSelectedItemId(R.id.navigation_interventions);
    }


    @Override
    protected void onResume() {
        super.onResume();
        User user = MyTools.GetUserInSession();
        ((TextView)findViewById(R.id.title)).setText(""+user);
    }

    @Override
    public void onBackPressed() {
        MyTools.confirmLogout(this);
        return;
    }

    MainViewModel mainViewModel;
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
        }
/*        else if(item.getItemId()==R.id.appSettings){
            startActivity(new Intent(this, SettingsActivity.class));
        }*/
        return true;
    }

    public void AddItem(View view){
        int num=1;
        if(view.getId()==R.id.btn_add_user)
            num=2;
        if(view.getId()==R.id.btn_add_client)
            num=3;

        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra("num", num);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
    }
    public void AddClient(View view) {
        Toast.makeText(this, "Open Activity Add Client..", Toast.LENGTH_SHORT).show();
    }
    public void RefreshList(View view) {
        //Toast.makeText(this, "Refresh list..", Toast.LENGTH_SHORT).show();
        mainViewModel.setSearch("");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_FILTRE_CODE){
            try {
                String codeClient = data.getStringExtra("codeClient");
                String codeSupervisor = data.getStringExtra("codeSupervisor");
                String dateDebutPrev = data.getStringExtra("dateDebutPrev");
                String dateDebutReel = data.getStringExtra("dateDebutReel");
                int status = data.getIntExtra("status",0);
                List<String> codesTechnicians =  data.getStringArrayListExtra("codesTechnicians");

                Log.i(TAG, "onActivityResult: "+codeClient+" "+dateDebutPrev+" "+status);

                Hashtable<String, Object> filter = new Hashtable();
                filter.put("codeClient", codeClient);
                filter.put("codeSupervisor", codeSupervisor);
                filter.put("dateDebutPrev", dateDebutPrev);
                filter.put("dateDebutReel", dateDebutReel);
                filter.put("status", status);

                filter.put("codesTechnicians", codesTechnicians);

                mainViewModel.setFilter(filter);
            }catch(Exception e){
                Log.i(TAG, "onActivityResult: "+e.getMessage());
            }

        }
    }



}