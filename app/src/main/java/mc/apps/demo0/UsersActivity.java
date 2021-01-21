package mc.apps.demo0;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import mc.apps.demo0.ui.clients.ClientsFragment;
import mc.apps.demo0.ui.users.UsersFragment;
import mc.apps.demo0.viewmodels.MainViewModel;

public class UsersActivity extends AppCompatActivity {
    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clients_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, UsersFragment.newInstance())
                    .commitNow();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Clients");

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setSearch("");
    }

    @Override
    public void onBackPressed() {
        returnDataAndFinish();
    }

    private void returnDataAndFinish() {
/*
        Intent intent = new Intent();
        intent.putExtra("data", (Serializable) mainViewModel.getSelected().getValue());
        setResult(RESULT_OK, intent);*/

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