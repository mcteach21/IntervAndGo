package mc.apps.interv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;

import mc.apps.interv.ui.select.SelectFragment;
import mc.apps.interv.viewmodels.MainViewModel;

public class SelectActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private int num; // 1: liste techniciens | 2 : liste superviseurs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);

        Intent intent = getIntent();
        num = intent.getIntExtra("num", 1);

        if (savedInstanceState == null)
            defineFragment(num);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(num==1?"Techniciens":"Superviseurs");

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setSearch("");

        mainViewModel.getUser().observe(this, selected -> {
            if(num == 2)
                returnDataAndFinish();
        });
    }

    private void defineFragment(int num) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SelectFragment(num)).commitNow();
    }

    @Override
    public void onBackPressed() {
        returnDataAndFinish();
    }

    private void returnDataAndFinish() {

        Intent intent = new Intent();
        if(num==1)
            intent.putExtra("data", (Serializable) mainViewModel.getSelected().getValue());
        else
            intent.putExtra("data", (Serializable) mainViewModel.getUser().getValue());

        setResult(RESULT_OK, intent);
        finish();
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
        int id = item.getItemId();
        if (id==android.R.id.home)
            returnDataAndFinish();

        return false;
    }
}