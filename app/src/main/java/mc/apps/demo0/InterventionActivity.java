package mc.apps.demo0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.model.Affectation;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class InterventionActivity extends AppCompatActivity {

    Intervention intervention;
    AutoCompleteTextView codeClient;
    EditText desc, dateDebut, dateFin, serviceCible, materielNecessaire, comment;
    RecyclerView tech_list;
    List<User> selected = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervention);

        setTitle("Intervention (Détails)");

        Intent intent = getIntent();
        intervention = (Intervention) intent.getSerializableExtra("intervention");
        if(intervention==null){
            finish();
        }

        //TextView title = findViewById(R.id.layout_title);
        Init();
    }
    private void Init() {
        codeClient = findViewById(R.id.edtCodeClient);
        desc = findViewById(R.id.edtDesc);
        dateDebut = findViewById(R.id.edtDateDebutPrev);
        dateFin = findViewById(R.id.edtDateFinPrev);
        serviceCible = findViewById(R.id.edtMaterielNecess);
        materielNecessaire = findViewById(R.id.edtMaterielNecess);
        comment = findViewById(R.id.edtComment);

        codeClient.setText(intervention.getClientId());
        desc.setText(intervention.getDescription());
        dateDebut.setText(intervention.getDateDebutPrevue());
        dateFin.setText(intervention.getDateFinPrevue());

        serviceCible.setText(intervention.getServiceEquipCible());
        comment.setText(intervention.getCommentaire());
        materielNecessaire.setText(intervention.getMaterielNecessaire());

        InitAffectations();
    }

    private void InitAffectations(){

        tech_list = findViewById(R.id.tech_list);
        tech_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2);
        tech_list.setLayoutManager(layoutManager2);
        SelectedUsersAdapter adapter = new SelectedUsersAdapter(
                selected,
                null,
                true
        );
        tech_list.setAdapter(adapter);
        /*mainViewModel.getSelected().observe(getActivity(), selected -> {
            adapter.refresh(selected);
        });*/
    }


}