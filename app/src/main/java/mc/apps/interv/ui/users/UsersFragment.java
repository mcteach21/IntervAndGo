package mc.apps.interv.ui.users;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.interv.R;
import mc.apps.interv.adapters.UsersAdapter;
import mc.apps.interv.dao.UserDao;
import mc.apps.interv.model.User;
import mc.apps.interv.viewmodels.MainViewModel;

public class UsersFragment extends Fragment {

    private static final String TAG = "tests";
    private MainViewModel mainViewModel;

    public static UsersFragment newInstance() {
        return new UsersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.clients_main_fragment, container, false);
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
        mainViewModel.getProfil().observe(
                getViewLifecycleOwner(),
                profil -> {
                   profil_filtre = profil;
                   refreshListAsync();
                }
        );
        mainViewModel.getSupervisor().observe(
                getViewLifecycleOwner(),
                supervisor_code -> {
                    supervisor_filtre = supervisor_code;
                    refreshListAsync();
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

    int profil_filtre = 0;
    String supervisor_filtre="";

    View root;

    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UsersAdapter(
                items,
                (position, item) -> {
                    mainViewModel.setUser((User) item);
                }
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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshListAsync() {
        UserDao dao = new UserDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, User.class);

            if(profil_filtre!=0)
                items = items.stream().filter(u->u.getProfilId()==profil_filtre).collect(Collectors.toList());

            if(!supervisor_filtre.isEmpty())
                items = items.stream().filter(u->u.getSupervisorId().equals(supervisor_filtre)).collect(Collectors.toList());

            loadList();
            swipeContainer.setRefreshing(false);
        });
    }

}