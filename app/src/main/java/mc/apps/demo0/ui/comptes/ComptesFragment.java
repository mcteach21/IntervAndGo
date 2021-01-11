package mc.apps.demo0.ui.comptes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.R;
import mc.apps.demo0.SupervisorActivity;
import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.adapters.UsersAdapter;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;

public class ComptesFragment extends Fragment {

    private ComptesViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(ComptesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_comptes, container, false);

        final TextView textView = root.findViewById(R.id.fragment_title);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("[Admin] "+s);
            }
        });
        return root;
    }
    View root;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Admin : Comptes");

        root = view;
        refreshListAsync(); //charger liste comptes!

        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Gestion liste Interventions
     */
    List<User> items = new ArrayList<User>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;

    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), LinearLayoutManager.VERTICAL));

        UsersAdapter adapter = new UsersAdapter(
                items,
                (position, item) -> {
                    Toast.makeText(root.getContext(), "click on : "+item.toString(), Toast.LENGTH_SHORT).show();
                }
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                root.getContext(), R.anim.layout_fall_down_animation
        ));

        // Lookup the swipe container view
        swipeContainer = root.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshListAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright
        );
    }
    private void refreshListAsync() {

        UserDao dao = new UserDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, User.class);
            loadList();
            swipeContainer.setRefreshing(false);
        });
    }
}