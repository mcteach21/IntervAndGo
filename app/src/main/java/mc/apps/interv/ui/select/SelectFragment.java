package mc.apps.interv.ui.select;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mc.apps.interv.R;
import mc.apps.interv.adapters.SelectedUsersAdapter;
import mc.apps.interv.adapters.UsersAdapter;
import mc.apps.interv.dao.UserDao;
import mc.apps.interv.model.User;
import mc.apps.interv.viewmodels.MainViewModel;

public class SelectFragment extends Fragment {
    private MainViewModel mainViewModel;
    private int num=1;

    public SelectFragment(int num) {
        this.num = num;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout = num==1?R.layout.select_main_fragment:R.layout.select_user_fragment;
        return inflater.inflate(layout, container, false);
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
        // liste users (tech|superv..)

        boolean select_option =(num==1);
        MainViewModel viewModel = (num==1)?mainViewModel:null;

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new UsersAdapter(
                items,
                (position, item) -> {
                    mainViewModel.setUser((User) item);
                },
                select_option,
                viewModel
        );
        recyclerView.setAdapter(adapter);
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

        if(num==1) {
            //selection list (tech)
            recyclerView_selected = root.findViewById(R.id.selected_items);
            GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 2);
            recyclerView_selected.setLayoutManager(layoutManager2);

             adapter2 = new SelectedUsersAdapter(
                selected,
                (position, item) -> {
                   // Toast.makeText(root.getContext(), "Selected : "+item.toString(), Toast.LENGTH_SHORT).show();
                },
                false
            );
            recyclerView_selected.setAdapter(adapter2);

            mainViewModel.getSelected().observe(getActivity(), selected -> {
                adapter2.refresh(selected);
            });
        }


    }
    private void refreshListAsync() {
        int profil_filter = (num==1)?3:2; //technicien/superviseur

        UserDao dao = new UserDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, User.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream()
                        .sorted((o1, o2)->o1.getLastname().compareTo(o2.getLastname()))
                        .filter(u->u.getProfilId()==profil_filter)       //filtre techniciens | superviseur..
                        .collect(Collectors.toList());
            }

            loadList();
            swipeContainer.setRefreshing(false);
        });
    }
}