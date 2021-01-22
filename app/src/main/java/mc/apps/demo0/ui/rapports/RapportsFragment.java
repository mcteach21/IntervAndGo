package mc.apps.demo0.ui.rapports;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.InterventionActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.viewmodels.MainViewModel;

public class RapportsFragment extends Fragment {

    private TextView title;
    MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rapports, container, false);

        title = root.findViewById(R.id.fragment_title);
        title.setText("Rapports");

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        refreshListAsync();
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel.getSearch().observe(
                getViewLifecycleOwner(),
                search -> {
                    if (search == null || search.length() == 0)
                        refreshListAsync();
                    else
                    if(adapter!=null)
                        adapter.getFilter().filter(search);
                }
        );
    }

    /**
     * Gestion liste Rapports
     */
    List<Intervention> items = new ArrayList<Intervention>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    InterventionsAdapter adapter;
    View root;

    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        //GridLayoutManager layoutManager = new GridLayoutManager(root.getContext(), 3);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new InterventionsAdapter(
                items,
                (position, item) -> {
                    Intent intent = new Intent(root.getContext(), InterventionActivity.class);
                    intent.putExtra("intervention", (Intervention)item);
                    startActivity(intent);
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

        InterventionDao dao = new InterventionDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, Intervention.class);
            //Log.i(TAG, "items : "+items);

            //order by date début
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream().filter(i->i.getDateDebutReelle()!=null && i.getDateFinReelle()!=null).sorted((o1, o2)->o1.getDateDebutReelle().compareTo(o2.getDateDebutReelle()))
                        .collect(Collectors.toList());
            }

            loadList();
            swipeContainer.setRefreshing(false);
            //runLayoutAnimation(recyclerView);
        });
    }
}