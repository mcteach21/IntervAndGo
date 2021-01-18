package mc.apps.demo0;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechnicianActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "demo";
    private DrawerLayout drawerLayout;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician);


        Toolbar toolbar = (Toolbar)findViewById(R.id.adminToolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        User user = getCurrentUser();
        ((TextView)findViewById(R.id.title)).setText(""+user);

/*        ((TextView)findViewById(R.id.header_user_name)).setText(""+user);
        ((TextView)findViewById(R.id.header_user_email)).setText(user.getEmail());*/

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private User getCurrentUser() {
        User user = (User) getIntent().getSerializableExtra("user");
        return user;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home :
                break;
            case R.id.nav_gallery:
                break;
            default:
                MyTools.confirmExit(this);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
}
