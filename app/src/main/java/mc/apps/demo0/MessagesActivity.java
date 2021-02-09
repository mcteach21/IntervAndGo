package mc.apps.demo0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.adapters.MessagesAdapter;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.dao.MessageDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.Message;

public class MessagesActivity extends AppCompatActivity {

    private static final String TAG = "tests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().hide();

        refreshListAsync();
    }

    @Override
    public void onBackPressed() {
        MessageDao dao = new MessageDao();
        for (Message message : messages) {
            message.setSeen((byte) 1);
            dao.update(message, (i, m) -> {
            });
        }
        super.onBackPressed();
    }

    List<Message> messages = new ArrayList<Message>();
    
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    MessagesAdapter adapter;

    private void loadList(){
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessagesAdapter(
                messages,
                (position, item) -> {

                }
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
               this, R.anim.layout_fall_down_animation
        ));

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshListAsync();
            }
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright
        );
    }
    private void refreshListAsync() {
        MessageDao dao = new MessageDao();
        dao.list((data, message) -> {
            messages = dao.Deserialize(data, Message.class);

            String current_code = MyTools.GetUserInSession().getCode();
            Log.i(TAG , "refreshListAsync: "+current_code);
            Log.i(TAG , "refreshListAsync: "+messages.size());
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                messages = messages.stream()
                        .sorted((o1, o2)->o2.getDateCreation().compareTo(o1.getDateCreation()))
                        .filter(x->x.getToUser().equals(current_code))
                        .collect(Collectors.toList());
            }
            Log.i(TAG , "refreshListAsync: "+messages.size());
            loadList();
            swipeContainer.setRefreshing(false);
        });
    }
}