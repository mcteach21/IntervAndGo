package mc.apps.demo0.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.ClientsActivity;
import mc.apps.demo0.InterventionActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.SupervisorActivity;
import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final int CODE_CLIENT_SELECT = 1304;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "tests" ;

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);

        return fragment;
    }

    int index = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        if (getArguments() != null)
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int layout = (index==1)?R.layout.fragment_superv_intervs:R.layout.fragment_superv_planif;
        root = inflater.inflate(layout, container, false);
        final TextView textView = root.findViewById(R.id.fragment_title);

        pageViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return root;
    }

    AutoCompleteTextView codeClient;
    EditText desc, dateDebut, dateFin, serviceCible, materielNecessaire, comment;
    RecyclerView tech_list;

    MainViewModel mainViewModel;
    List<User> selected = new ArrayList();

    View root;
    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        if (index==1){ //liste interventions
            refreshListAsync();
            root.findViewById(R.id.btn_refresh_list).setOnClickListener(v->refreshListAsync());

            mainViewModel.getSearch().observe(
                    getViewLifecycleOwner(),
                    search -> {
                        if (search.isEmpty())
                            refreshListAsync();
                        else
                        if(adapter!=null)
                            adapter.getFilter().filter(search);
                    }
            );
            mainViewModel.getFilter().observe(
                    getViewLifecycleOwner(),
                    filter -> {
                        if (filter.isEmpty())
                            refreshListAsync();
                        else
                        if(adapter!=null)
                            adapter.setFilter(filter);
                    }
            );
        }
        if (index==2){ //Planifier Intervention

            initClientAutocomplete(root);
            initListTech(root);

            Button btnadd = root.findViewById(R.id.btn_add);
            btnadd.setOnClickListener(view -> {
               addIntervention(root);
            });
        }
    }


    /**
     * Liste Interventions
     * @param root
     */

    List<Intervention> items = new ArrayList<Intervention>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    InterventionsAdapter adapter;

    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InterventionsAdapter(
                items,
                (position, item) -> {
                    Intent intent = new Intent(getActivity(), InterventionActivity.class);
                    intent.putExtra("intervention", (Intervention)item);
                    startActivity(intent);
                }
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                getActivity(), R.anim.layout_fall_down_animation
        ));

        swipeContainer = root.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> refreshListAsync());

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
            Log.i(TAG, "items : "+items);

            //order by date début
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream().sorted((o1, o2)->o1.getDateDebutPrevue().compareTo(o2.getDateDebutPrevue()))
                        .collect(Collectors.toList());

                Log.i(TAG, "refreshListAsync: ordered?");
            }

            loadList();
            swipeContainer.setRefreshing(false);
            //runLayoutAnimation(recyclerView);
        });
    }


    /**
     * Planification
     * @param root
     */
    private void addIntervention(View root) {
        codeClient = root.findViewById(R.id.txtCodeClient);
        desc = root.findViewById(R.id.edtDesc);
        dateDebut = root.findViewById(R.id.edtDateDebutPrev);
        dateFin = root.findViewById(R.id.edtDateFinPrev);
        serviceCible = root.findViewById(R.id.edtMaterielNecess);
        materielNecessaire = root.findViewById(R.id.edtMaterielNecess);
        comment = root.findViewById(R.id.edtComment);

        //technicien(s) affecté(s)
        SelectedUsersAdapter adapter = (SelectedUsersAdapter) tech_list.getAdapter();
        List<User> technicians = adapter.getItems();

        Intervention interv = new Intervention(
                "code", codeClient.getText().toString(),
                desc.getText().toString(), dateDebut.getText().toString(), dateFin.getText().toString(),
                comment.getText().toString(),
                "mat nécess..",serviceCible.getText().toString(),
                "code superv",
                technicians
        );

        InterventionDao dao = new InterventionDao();
        dao.add(interv, (items, message) -> {
            Log.i(TAG, "onCreate: "+message);
            Toast.makeText(root.getContext(), "Intervention planifiée!", Toast.LENGTH_LONG).show();
        });
        resetFields(root); //reinitialiser form planfication!
    }
    private void initClientAutocomplete(View root) {
        ClientDao dao = new ClientDao();

        dao.list((data, message) -> {
            List<Client> items = dao.Deserialize(data, Client.class);
            ArrayAdapter<Client> adapter = new ArrayAdapter<Client>(
                    root.getContext(),
                    android.R.layout.select_dialog_item,
                    items);

            codeClient = root.findViewById(R.id.txtCodeClient);
            codeClient.setThreshold(1);       //will start working from first character
            codeClient.setAdapter(adapter);
            codeClient.setTextColor(Color.WHITE);
        });

        root.findViewById(R.id.btn_clients_list).setOnClickListener(
                v->{
                    //Toast.makeText(root.getContext(), "open clients list..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(root.getContext(), ClientsActivity.class);
                    startActivityForResult(intent, CODE_CLIENT_SELECT );
                }
        );

        mainViewModel.getClient().observe(getActivity(), selected -> {
            codeClient.setText(selected.getCode());
        });

    }
    private void initListTech(View root){
        tech_list = root.findViewById(R.id.tech_list);
        tech_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 2);
        tech_list.setLayoutManager(layoutManager2);

        SelectedUsersAdapter adapter = new SelectedUsersAdapter(
                selected,
                null,
                true
        );
        tech_list.setAdapter(adapter);
        mainViewModel.getSelected().observe(getActivity(), selected -> {
                    adapter.refresh(selected);
                });
    }

    private void resetFields(View root) {
        codeClient.getText().clear();
        desc.getText().clear();
        dateDebut.getText().clear();
        dateFin.getText().clear();
        serviceCible.getText().clear();
        comment.getText().clear();
        materielNecessaire.getText().clear();

        ((SelectedUsersAdapter)tech_list.getAdapter()).getItems().clear();
        ((SelectedUsersAdapter)tech_list.getAdapter()).notifyDataSetChanged();
    }
}