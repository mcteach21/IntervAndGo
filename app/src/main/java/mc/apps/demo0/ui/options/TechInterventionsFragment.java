package mc.apps.demo0.ui.options;

import androidx.fragment.app.Fragment;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.R;
import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechInterventionsFragment extends Fragment {
    private MainViewModel mainViewModel;
    private TextView title;

    private View root ;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_technicien, container, false);

        title = root.findViewById(R.id.fragment_title);
        title.setText("Interventions");



        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return root;
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
        refreshListAsync();
    }

    /**
     * Gestion liste Interventions
     */
    List<Intervention> items = new ArrayList<Intervention>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    InterventionsAdapter adapter;

    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        //val layoutManager = GridLayoutManager(this, 3)

        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), LinearLayoutManager.VERTICAL));

        adapter = new InterventionsAdapter(
                items,
                (position, item) -> {
                    Toast.makeText(root.getContext(), "click on : "+item.toString(), Toast.LENGTH_SHORT).show();
                },
                true
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

    String pattern = "yyyy-MM-dd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private String getDate(String dateString){
       return dateString.split(" ")[0];
    }
    private String getCurrentDate(){
        return simpleDateFormat.format(new Date());
    }
    private void refreshListAsync() {

        InterventionDao dao = new InterventionDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, Intervention.class);

            //order by date début
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream()
                        .sorted((o1, o2)->o1.getDateDebutPrevue().compareTo(o2.getDateDebutPrevue()))
                        .filter(i->getDate(i.getDateDebutPrevue()).equals(getCurrentDate()))
                        .collect(Collectors.toList());

                //Log.i(TAG, "refreshListAsync: ordered?");
            }

            loadList();
            swipeContainer.setRefreshing(false);
            //runLayoutAnimation(recyclerView);
        });
    }
}
