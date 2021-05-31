package mc.apps.interv;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import mc.apps.interv.ui.users.UsersFragment;
import mc.apps.interv.viewmodels.MainViewModel;

public class UsersActivity extends AppCompatActivity {
    MainViewModel mainViewModel;
    private static final int CODE_USER_SELECT = 1404;

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

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setSearch("");
        mainViewModel.getUser().observe(
                this,
                user -> {
                    Intent intent = new Intent();
                    intent.putExtra("data", user);
                    setResult(RESULT_OK, intent);
                    finish();
                }
        );

        Intent intent = getIntent();
        int profil_filter = intent.getIntExtra("profil",0);
        mainViewModel.setProfil(profil_filter);

        setTitle((profil_filter==2)?"Superviseurs":(profil_filter==3)?"Techniciens":"Utilisateurs");
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