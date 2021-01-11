package mc.apps.demo0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.adapters.ClientsAdapter;
import mc.apps.demo0.adapters.UsersAdapter;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.User;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        refreshListAsync();
    }
    /**
     * Gestion liste..
     */
    List<?> items = new ArrayList();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;

    private void loadList(){
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        ClientsAdapter adapter = new ClientsAdapter(
                (List<Client>) items,
                (position, item) -> {
                    Toast.makeText(SearchActivity.this, "click on : "+item.toString(), Toast.LENGTH_SHORT).show();
                }
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                this, R.anim.layout_fall_down_animation
        ));
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> refreshListAsync());
        swipeContainer.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_orange_light,  android.R.color.holo_blue_bright );
    }
    private void refreshListAsync() {
        ClientDao dao = new ClientDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, Client.class);
            loadList();
            swipeContainer.setRefreshing(false);
        });
    }
}