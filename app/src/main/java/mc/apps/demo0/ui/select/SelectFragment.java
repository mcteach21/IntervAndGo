package mc.apps.demo0.ui.select;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mc.apps.demo0.R;
import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.adapters.UsersAdapter;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class SelectFragment extends Fragment {
    private MainViewModel mainViewModel;
    public static SelectFragment newInstance() {
        return new SelectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_main_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
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
     * Gestion liste..
     */
    List<User> items = new ArrayList<User>();
    List<User> selected = new ArrayList<User>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView, recyclerView_selected;

    UsersAdapter adapter;
    SelectedUsersAdapter adapter2;
    View root;

    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView_selected = root.findViewById(R.id.selected_items);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView_selected.setLayoutManager(layoutManager2);

        adapter = new UsersAdapter(
                items,
                (position, item) -> {
                    Toast.makeText(root.getContext(), "Selected : "+item.toString(), Toast.LENGTH_SHORT).show();
                },
                true,
                mainViewModel
        );
        recyclerView.setAdapter(adapter);

        adapter2 = new SelectedUsersAdapter(
                selected,
                (position, item) -> {
                    Toast.makeText(root.getContext(), "Selected : "+item.toString(), Toast.LENGTH_SHORT).show();
                },
                false
        );
        recyclerView_selected.setAdapter(adapter2);
        mainViewModel.getSelected().observe(getActivity(), selected -> {
            Log.i("tests", "loadList: "+selected);
            adapter2.refresh(selected);
        });


        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                root.getContext(), R.anim.layout_fall_down_animation
        ));

        swipeContainer = root.findViewById(R.id.swipeContainer);
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
        UserDao dao = new UserDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, User.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream()
                        .sorted((o1, o2)->o1.getLastname().compareTo(o2.getLastname()))
                        .filter(u->u.getProfilId()>2)       //filtre techniciens
                        .collect(Collectors.toList());
            }

            loadList();
            swipeContainer.setRefreshing(false);
        });
    }
}