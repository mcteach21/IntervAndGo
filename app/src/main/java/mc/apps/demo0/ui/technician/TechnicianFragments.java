package mc.apps.demo0.ui.technician;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.InterventionActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.adapters.ImagesAdapter;
import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.dao.AffectationDao;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Affectation;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechnicianFragments extends Fragment {
    private static final String TAG = "tests";
    private static final int TECH_INTERV_CODE = 1000;
    private MainViewModel mainViewModel;
    private View root ;
    private int[] fragments_layouts = {
            R.layout.technician_intervs_fragment,
            R.layout.technician_rapport_fragment,
            R.layout.technician_intervs_fragment   //R.layout.technician_histo_fragment
    };
    private String[] fragments_titles = {
           "Interventions du Jour",
           "Intervention", //Saisir Rapport
           "Historique Interventions/Rapports"
    };

    private static Intervention intervention=null;
    private static int num=0;
    public static TechnicianFragments newInstance(int num) {
        TechnicianFragments.num = num;
        TechnicianFragments.intervention = null;
        return new TechnicianFragments();
    }
    public static TechnicianFragments newInstance(int num, Intervention intervention) {
        TechnicianFragments.num = num;
        TechnicianFragments.intervention = intervention;
        return new TechnicianFragments();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(fragments_layouts[num], container, false);
        TextView title = root.findViewById(R.id.fragment_title);
        title.setText(fragments_titles[num]);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        if(num==0 || num==2) {
            //Recherche / Liste Interventions..
            mainViewModel.getSearch().observe(
                    getViewLifecycleOwner(),
                    search -> {
                        if (search == null || search.length() == 0)
                            refreshListAsync();
                        else if (adapter != null)
                            adapter.getFilter().filter(search);
                    }
            );
            refreshListAsync();
        }else if(num==1){
            //Ajouter Rapport
            initCurrentIntervention(root); //AutoCompletion sur Champ CodeClient!
            initListPhotos(root);   //liste photos / Rapport
            Button btnadd = root.findViewById(R.id.btn_add);
            btnadd.setOnClickListener(view -> {
                addRapport(root);
            });
        }
    }



    /**
     * Saisie Rapport / Technicien
     */
    EditText codeClient, codeIntervention, descIntervention, comment, dateDebut, dateFin, dateDebutR, dateFinR;
    Spinner statutChoice;
    int statut;

    private void initCurrentIntervention(View root) {
        codeIntervention = root.findViewById(R.id.edtCodeInterv);
        descIntervention = root.findViewById(R.id.edtDescInterv);
        codeClient = root.findViewById(R.id.txtCodeClient);
        dateDebut = root.findViewById(R.id.edtDateDebutPrev);
        dateFin = root.findViewById(R.id.edtDateFinPrev);
        dateDebutR = root.findViewById(R.id.edtDateDebutReel);
        dateFinR = root.findViewById(R.id.edtDateFinReel);

        comment = root.findViewById(R.id.edtComment);
        statutChoice = root.findViewById(R.id.statutChoice);

        if(TechnicianFragments.intervention!=null){
            codeIntervention.setText(TechnicianFragments.intervention.getCode());
            descIntervention.setText(TechnicianFragments.intervention.getDescription());
            codeClient.setText(TechnicianFragments.intervention.getClientId());

            dateDebut.setText(TechnicianFragments.intervention.getDateDebutPrevue());
            dateFin.setText(TechnicianFragments.intervention.getDateFinPrevue());

            comment.setText(TechnicianFragments.intervention.getCommentaire());
            statutChoice.setSelection(TechnicianFragments.intervention.getStatutId()-1);
        }
        mainViewModel.getIntervention().observe(
                getViewLifecycleOwner(),
                intervention -> {
                    if (intervention != null){
                        TechnicianFragments.intervention = intervention;

                        codeIntervention.setText(intervention.getCode());
                        descIntervention.setText(intervention.getDescription());
                        codeClient.setText(intervention.getClientId());
                        dateDebut.setText(intervention.getDateDebutPrevue());
                        dateFin.setText(intervention.getDateFinPrevue());
                        comment.setText(intervention.getCommentaire());
                        statutChoice.setSelection(intervention.getStatutId()-1);
                    }
                }
        );
    }

    private void addRapport(View root) {
        if(TechnicianFragments.intervention==null)
            return;

        dateDebutR = root.findViewById(R.id.edtDateDebutPrev);
        dateFinR = root.findViewById(R.id.edtDateFinPrev);
        comment = root.findViewById(R.id.edtComment);

        TechnicianFragments.intervention.setCommentaire(comment.getText().toString());
        TechnicianFragments.intervention.setDateDebutReelle(dateDebutR.getText().toString());
        TechnicianFragments.intervention.setDateFinReelle(dateFinR.getText().toString());

        statut = (int) (statutChoice.getSelectedItemId()+1);
        TechnicianFragments.intervention.setStatutId(statut);

        InterventionDao dao = new InterventionDao();
        dao.update(TechnicianFragments.intervention, (items, message)->{
            Toast.makeText(root.getContext(), "Rapport ajouté avec succès!", Toast.LENGTH_SHORT).show();
            mainViewModel.setNum(0);
        });

        resetFields(); //reinitialiser form
    }

    RecyclerView photos_list;
    List<Uri> images = new ArrayList();
    private void initListPhotos(View root){
        photos_list = root.findViewById(R.id.photos_list);
        photos_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 4);
        photos_list.setLayoutManager(layoutManager2);
        ImagesAdapter adapter = new ImagesAdapter(
                images,
                null
        );
        photos_list.setAdapter(adapter);
        mainViewModel.getImages().observe(getActivity(), images -> {
            adapter.refresh(images);
        });
    }

    private void resetFields() {
        codeIntervention.getText().clear();
        descIntervention.getText().clear();
        codeClient.getText().clear();
        dateDebut.getText().clear();
        dateFin.getText().clear();
        statutChoice.setSelection(0);
        dateDebutR.getText().clear();
        dateFinR.getText().clear();
        comment.getText().clear();
    }

    /**
     * Gestion liste Interventions jour / Technicien
     */
    List<Intervention> items = new ArrayList<Intervention>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    InterventionsAdapter adapter;

    TextView noResult;
    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new InterventionsAdapter(
                items,
                (position, item) -> {
                    Intent intent = new Intent(root.getContext(), InterventionActivity.class);
                    intent.putExtra("intervention", (Intervention)item);
                    intent.putExtra("rapport", true);
                    getActivity().startActivityForResult(intent, TECH_INTERV_CODE);
                },
                true
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

    String pattern = "yyyy-MM-dd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private String getDate(String dateString){
        return dateString.split(" ")[0];
    }
    private String getCurrentDate(){
        return simpleDateFormat.format(new Date());
    }

    private void refreshListAsync() {
        noResult = root.findViewById(R.id.noResult);
        InterventionDao dao = new InterventionDao();

        String tech_code = MyTools.GetUserInSession().getCode();
        dao.findByTech(tech_code, (data, message) -> {
            items = dao.Deserialize(data, Intervention.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                if(num==0) {
                    items = items.stream()
                            .sorted((o1, o2) -> o1.getDateDebutPrevue().compareTo(o2.getDateDebutPrevue()))
                            .filter(i -> getDate(i.getDateDebutPrevue()).equals(getCurrentDate()) && i.getStatutId() != 5)
                            .collect(Collectors.toList());
                }else{
                    items = items.stream()
                            .sorted((o1, o2) -> o2.getDateDebutPrevue().compareTo(o1.getDateDebutPrevue()))
                            .filter(i->i.getStatutId() == 5)
                            .collect(Collectors.toList());
                }
            }

            noResult.setVisibility(items.size()>0?View.GONE:View.VISIBLE);
            loadList();
            swipeContainer.setRefreshing(false);
        });
    }

}