package mc.apps.demo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.dao.StatsDao;
import mc.apps.demo0.model.ItemTotal;
import mc.apps.demo0.ui.main.ChartFragment;
import mc.apps.demo0.viewmodels.MainViewModel;

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