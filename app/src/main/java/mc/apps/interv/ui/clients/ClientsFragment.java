package mc.apps.interv.ui.clients;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import mc.apps.interv.adapters.ClientsAdapter;
import mc.apps.interv.dao.AdressDao;
import mc.apps.interv.dao.ClientDao;
import mc.apps.interv.dao.ContratDao;
import mc.apps.interv.dao.Dao;
import mc.apps.interv.model.Adress;
import mc.apps.interv.model.Client;
import mc.apps.interv.model.Contrat;
import mc.apps.interv.viewmodels.MainViewModel;

public class ClientsFragment extends Fragment {
    private static final String TAG = "tests";
    private MainViewModel mainViewModel;

    public static ClientsFragment newInstance() {
        return new ClientsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout = R.layout.clients_main_fragment;
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
    List<Client> items = new ArrayList<Client>();
    List<Client> selected = new ArrayList<Client>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView, recyclerView_selected;

    ClientsAdapter adapter;
    //SelectedUsersAdapter adapter2;
    View root;
    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        //recyclerView_selected = root.findViewById(R.id.selected_items);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        //GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 2);

        recyclerView.setLayoutManager(layoutManager);
        //recyclerView_selected.setLayoutManager(layoutManager2);

        adapter = new ClientsAdapter(
                items,
                (position, item) -> {
                    //Toast.makeText(root.getContext(), "Selected : "+item.toString(), Toast.LENGTH_SHORT).show();
                    mainViewModel.setClient((Client) item);

                    /*Client client = (Client) item;
                    getClientAdress(client);*/
                }
        );
        recyclerView.setAdapter(adapter);

       /* adapter2 = new SelectedUsersAdapter(
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
        });*/


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

    private void getClientAdress(Client client) {
        AdressDao dao = new AdressDao();
        dao.ofClient(client.getCode(), new Dao.OnSuccess() {
            @Override
            public void result(List<?> items, String message) {
                List<Adress> adresses = dao.Deserialize(items, Adress.class);
                for (Adress adress : adresses)
                   client.addAdress(adress);
            }
        });
    }
    private void getClientContrat(Client client) {
        ContratDao dao = new ContratDao();
        dao.ofClient(client.getCode(), (items, message) -> {
            List<Contrat> contrats = dao.Deserialize(items, Contrat.class);
            for (Contrat contrat : contrats)
                client.addContrat(contrat);
        });
    }

    private void refreshListAsync() {
        ClientDao dao = new ClientDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, Client.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream()
                        .sorted((o1, o2)->o1.getNom().compareTo(o2.getNom()))
                        .collect(Collectors.toList());
            }

            // ajout adresses+contrats!
            for (Client client : items) {
                getClientAdress(client);
                getClientContrat(client);
            }

            loadList();
            swipeContainer.setRefreshing(false);
        });
    }

}