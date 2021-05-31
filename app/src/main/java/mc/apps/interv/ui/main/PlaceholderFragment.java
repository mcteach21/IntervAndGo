package mc.apps.interv.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.interv.ChartActivity;
import mc.apps.interv.ClientsActivity;
import mc.apps.interv.InterventionActivity;
import mc.apps.interv.R;
import mc.apps.interv.ShowInMapActivity;
import mc.apps.interv.TechniciansActivity;
import mc.apps.interv.adapters.InterventionsAdapter;
import mc.apps.interv.adapters.SelectedUsersAdapter;
import mc.apps.interv.dao.AffectationDao;
import mc.apps.interv.dao.InterventionDao;
import mc.apps.interv.libs.MyTools;
import mc.apps.interv.model.Intervention;
import mc.apps.interv.model.User;
import mc.apps.interv.viewmodels.MainViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final int CODE_CLIENT_SELECT = 1000;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "tests" ;

    private PageViewModel pageViewModel;
    private MainViewModel mainViewModel;
    private List<User> selected = new ArrayList();
    private View root;

    private EditText codeClient, codeSupervisor, desc, dateDebut, dateFin, serviceCible, materielNecessaire, consignes;
    private RecyclerView tech_list;
    private Button btnadd, btnlist, btngps, btnstats;

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

        int[] fragments_layouts = {
                R.layout.fragment_superv_intervs,
                R.layout.fragment_superv_planif,
                R.layout.fragment_superv_techs
        };
        int layout = fragments_layouts[index-1];
        root = inflater.inflate(layout, container, false);
        final TextView textView = root.findViewById(R.id.fragment_title);

        pageViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return root;
    }

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
        else if (index==2){ //Planifier Intervention

            codeClient = root.findViewById(R.id.txtCodeClient);
            codeSupervisor = root.findViewById(R.id.edtSupervisor);
            codeSupervisor.setText(MyTools.GetUserInSession().getCode());
            root.findViewById(R.id.textInputLayout20).setVisibility(View.GONE);
            root.findViewById(R.id.btn_superv_list).setVisibility(View.GONE);

            desc = root.findViewById(R.id.edtDesc);
            dateDebut = root.findViewById(R.id.edtDateDebutPrev);
            dateFin = root.findViewById(R.id.edtDateFinPrev);
            serviceCible = root.findViewById(R.id.edtServiceCible);
            materielNecessaire = root.findViewById(R.id.edtMaterielNecess);
            consignes = root.findViewById(R.id.edtConsignes);

            btnadd = root.findViewById(R.id.btn_add);

            root.findViewById(R.id.btn_clients_list).setOnClickListener(
                    v->{
                        Intent intent = new Intent(root.getContext(), ClientsActivity.class);
                        startActivityForResult(intent, CODE_CLIENT_SELECT);
                    }
            );
            mainViewModel.getClient().observe(
                    getViewLifecycleOwner(),
                    selected -> {
                        codeClient.setText(selected.getCode());
                    }
            );

            mainViewModel.getUser().observe(
                    getViewLifecycleOwner(),
                    selected -> {
                        codeSupervisor.setText(selected.getCode());
                    }
            );

            btnadd.setOnClickListener(view -> {
               addIntervention(root);
            });

            initListTech(root);
        }else{
            //Suivi!!

            btnlist = root.findViewById(R.id.tech_btn_list);
            btngps = root.findViewById(R.id.tech_btn_gps);
            btnstats = root.findViewById(R.id.tech_btn_stats);

            btnlist.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), TechniciansActivity.class));
            });
            btngps.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), ShowInMapActivity.class);
                intent.putExtra("supervisor_filter", MyTools.GetUserInSession().getCode());
                startActivity(intent);
            });
            btnstats.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), ChartActivity.class));
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
                items = items.stream()
                        .sorted((o1, o2)->o2.getDateCreation().compareTo(o1.getDateCreation()))
                        .collect(Collectors.toList());
            }

            loadList();
            swipeContainer.setRefreshing(false);
        });
    }


    /**
     * Planification
     * @param root
     */

    private boolean checkForm(View root) {
        return !(codeClient.getText().toString().isEmpty() ||
                codeSupervisor.getText().toString().isEmpty() ||
                desc.getText().toString().isEmpty()
                || dateDebut.getText().toString().isEmpty());
    }
    private void addIntervention(View root) {
        boolean ok = checkForm(root);
        if(!ok){
            Toast.makeText(getContext(), "Des champs obligatoires non renseignés!!", Toast.LENGTH_SHORT).show();
            return;
        }


        //technicien(s) affecté(s)
        SelectedUsersAdapter adapter = (SelectedUsersAdapter) tech_list.getAdapter();
        List<User> technicians = adapter.getItems();

        Intervention interv = new Intervention(
                MyTools.getCurrentDateCode(),
                codeClient.getText().toString(),
                desc.getText().toString(), dateDebut.getText().toString(), dateFin.getText().toString(),
                consignes.getText().toString(),
                materielNecessaire.getText().toString(),
                serviceCible.getText().toString(),
                MyTools.GetUserInSession().getCode(),
                technicians
        );

        InterventionDao dao = new InterventionDao();
        dao.add(interv, (items, message) -> {
            Toast.makeText(root.getContext(), "Intervention planifiée!", Toast.LENGTH_LONG).show();
            addAffectaions(interv);

            mainViewModel.setNum(0);
        });
        resetFields(root); //reinitialiser form planfication!
    }

    private void addAffectaions(Intervention interv) {
        AffectationDao dao = new AffectationDao();
        dao.add(interv, (items, message) -> {
            //Toast.makeText(root.getContext(), "Intervention affectations ok.", Toast.LENGTH_LONG).show();
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
        consignes.getText().clear();
        materielNecessaire.getText().clear();

        ((SelectedUsersAdapter)tech_list.getAdapter()).getItems().clear();
        ((SelectedUsersAdapter)tech_list.getAdapter()).notifyDataSetChanged();
    }
}