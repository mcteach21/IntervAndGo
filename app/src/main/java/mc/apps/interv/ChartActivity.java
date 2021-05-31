package mc.apps.interv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import mc.apps.interv.ui.main.ChartFragment;
import mc.apps.interv.viewmodels.MainViewModel;

public class ChartActivity extends AppCompatActivity {
    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);

        if (savedInstanceState == null)
           defineFragment(1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Statistiques");

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void defineFragment(int num) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, ChartFragment.newInstance(num)).commitNow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
            finish();

        if (id==R.id.item_menu_stats_1)
            defineFragment(1);
        if (id==R.id.item_menu_stats_2)
            defineFragment(2);

        return false;
    }


}