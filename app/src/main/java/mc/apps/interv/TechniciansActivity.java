package mc.apps.interv;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import mc.apps.interv.libs.MyTools;
import mc.apps.interv.ui.users.UsersFragment;
import mc.apps.interv.viewmodels.MainViewModel;

public class TechniciansActivity extends AppCompatActivity {
    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, UsersFragment.newInstance())
                    .commitNow();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Techniciens/Superviseur");

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setSearch("");
        mainViewModel.setProfil(3); //tech
        mainViewModel.setSupervisor( MyTools.GetUserInSession().getCode());

    }

    @Override
    public void onBackPressed() {
        returnDataAndFinish();
    }

    private void returnDataAndFinish() {
        finish();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
            returnDataAndFinish();

        return false;
    }
}