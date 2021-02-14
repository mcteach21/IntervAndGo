package mc.apps.demo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;

import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.ui.item.ItemFragment;
import mc.apps.demo0.viewmodels.MainViewModel;

public class ItemActivity extends AppCompatActivity {
    private static final String TAG = "tests";
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_activity);




        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item");
        if(item==null)
            finish();

        setTitle(item.getClass().getSimpleName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setItem(item);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, ItemFragment.newInstance()).commitNow();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
            finish();

        return false;
    }

}