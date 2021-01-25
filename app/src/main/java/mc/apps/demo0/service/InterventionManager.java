package mc.apps.demo0.service;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.ClientsActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.dao.AffectationDao;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class InterventionManager {
    private static final String TAG = "tests";

    AutoCompleteTextView codeClient;
    EditText desc, dateDebut, dateFin, serviceCible, materielNecessaire, comment;
    RecyclerView tech_list;

    MainViewModel mainViewModel;
    List<User> selected = new ArrayList();
    Activity activity;

    public InterventionManager(Activity activity){
        this.activity = activity;
        mainViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(MainViewModel.class);

    }

    public void prepareAddIntervention(View root, Class<?> backActivity) {
        initClientAutocomplete(root);
        initListTech(root);

        Button btnadd = root.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(view -> {
            addIntervention(root);
            root.getContext().startActivity(new Intent(root.getContext() , backActivity));
            Log.i(TAG, "prepareAddIntervention : back to "+backActivity.getSimpleName());
        });
    }

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
                "INT"+MyTools.getCurrentDateCode(),
                codeClient.getText().toString(),
                desc.getText().toString(),
                dateDebut.getText().toString(),
                dateFin.getText().toString(),
                comment.getText().toString(),
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
    }
    private void addAffectaions(Intervention interv) {
        AffectationDao dao = new AffectationDao();
        dao.add(interv, (items, message) -> {
            Log.i(TAG, "Intervention affectations ok.");
        });
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

        mainViewModel.getClient().observe((LifecycleOwner) activity, selected -> {
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
        mainViewModel.getSelected().observe((LifecycleOwner) activity, selected -> {
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
