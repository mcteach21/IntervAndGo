package mc.apps.demo0;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.User;
import mc.apps.demo0.ui.main.SectionsPagerAdapter;

public class SupervisorActivity extends AppCompatActivity {
    private static final String TAG = "tests";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Action..", Snackbar.LENGTH_LONG)
                        .setAction("Action",
                                (v)-> Toast.makeText(SupervisorActivity.this, "Not Implemented yet!", Toast.LENGTH_SHORT).show()
                        ).show();
            }
        });*/

        //current user
        User user = getCurrentUser();
        //setTitle(R.string.title_activity_supervisor+" : "+user.getPrenom());
        String profil = getString(R.string.title_activity_supervisor);
        ((TextView)findViewById(R.id.title)).setText(profil+" : "+user.getPrenom());
    }

    private User getCurrentUser() {
        User user = (User) getIntent().getSerializableExtra("user");
        return user;
    }

    @Override
    public void onBackPressed() {
        MyTools.confirmExit(this);
        return;
    }
}