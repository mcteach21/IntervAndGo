package mc.apps.interv.service;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mc.apps.interv.R;
import mc.apps.interv.adapters.SelectedUsersAdapter;
import mc.apps.interv.dao.AffectationDao;
import mc.apps.interv.dao.InterventionDao;
import mc.apps.interv.libs.MyTools;
import mc.apps.interv.model.Intervention;
import mc.apps.interv.model.User;
import mc.apps.interv.viewmodels.MainViewModel;

public class InterventionManager {
    private static final String TAG = "tests";

    EditText codeClient, codeSupervisor, desc, dateDebut, dateFin, serviceCible, materielNecessaire, consignes;
    RecyclerView tech_list;
    Button btnadd;

    MainViewModel mainViewModel;
    List<User> selected = new ArrayList();
    Activity activity;

    public InterventionManager(Activity activity){
        this.activity = activity;
        mainViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(MainViewModel.class);
    }

    private void getForm(View root) {
        codeClient = root.findViewById(R.id.txtCodeClient);
        codeSupervisor = root.findViewById(R.id.edtSupervisor);
        desc = root.findViewById(R.id.edtDesc);
        dateDebut = root.findViewById(R.id.edtDateDebutPrev);
        dateFin = root.findViewById(R.id.edtDateFinPrev);
        serviceCible = root.findViewById(R.id.edtServiceCible);
        materielNecessaire = root.findViewById(R.id.edtMaterielNecess);
        consignes = root.findViewById(R.id.edtConsignes);
    }

    private boolean checkForm(View root) {
        getForm(root);
        return !(codeClient.getText().toString().isEmpty() ||
                codeSupervisor.getText().toString().isEmpty() ||
                desc.getText().toString().isEmpty()
                || dateDebut.getText().toString().isEmpty());
    }


    public void prepareAddIntervention(View root, Class<?> backActivity) {
        getForm(root);

        btnadd = root.findViewById(R.id.btn_add);
        mainViewModel.getClient().observe((LifecycleOwner) activity, selected -> {
            codeClient.setText(selected.getCode());
        });
        mainViewModel.getUser().observe((LifecycleOwner) activity, selected -> {
            codeSupervisor.setText(selected.getCode());
        });

        initListTech(root);
        btnadd.setOnClickListener(view -> {

            Intent intent = new Intent(root.getContext(), backActivity);
            intent.putExtra("num",2);

            if(addIntervention(root)) {
                root.getContext().startActivity(intent);

            }else{
                Toast.makeText(activity, "Des champs obligatoires non renseignés!!", Toast.LENGTH_SHORT).show();
            }

            //Log.i(TAG, "prepareAddIntervention : back to "+backActivity.getSimpleName());
        });
    }

    private boolean addIntervention(View root) {
        boolean ok = checkForm(root);
        if(!ok)
            return false;

        //technicien(s) affecté(s)
        SelectedUsersAdapter adapter = (SelectedUsersAdapter) tech_list.getAdapter();
        List<User> technicians = adapter.getItems();

        Intervention interv = new Intervention(
                "INT"+MyTools.getCurrentDateCode(),
                codeClient.getText().toString(),
                desc.getText().toString(),
                dateDebut.getText().toString(),
                dateFin.getText().toString(),
                consignes.getText().toString(),
                materielNecessaire.getText().toString(),
                serviceCible.getText().toString(),
                MyTools.GetUserInSession().getCode(),  //current user code...
                technicians
        );

        InterventionDao dao = new InterventionDao();
        dao.add(interv, (items, message) -> {
            Log.i(TAG, "onCreate: "+message);
            Toast.makeText(root.getContext(), "Intervention planifiée!", Toast.LENGTH_LONG).show();
            addAffectaions(interv);
        });
        resetFields(root); //reinitialiser form planfication!
        return true;
    }
    private void addAffectaions(Intervention interv) {
        AffectationDao dao = new AffectationDao();
        dao.add(interv, (items, message) -> {
            Log.i(TAG, "Intervention affectations ok.");

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
        mainViewModel.getSelected().observe((LifecycleOwner) activity, selected -> {
            adapter.refresh(selected);
        });
    }

    private void resetFields(View root) {
        codeClient.getText().clear();
        codeSupervisor.getText().clear();
        desc.getText().clear();
        dateDebut.getText().clear();
        dateFin.getText().clear();
        serviceCible.getText().clear();
        consignes.getText().clear();
        materielNecessaire.getText().clear();

        SelectedUsersAdapter adapter = (SelectedUsersAdapter)tech_list.getAdapter();
        adapter.getItems().clear();
        adapter.notifyDataSetChanged();
    }
}
