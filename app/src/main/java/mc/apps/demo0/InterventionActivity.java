package mc.apps.demo0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Intervention;

public class InterventionActivity extends AppCompatActivity {

    Intervention intervention;
    TextView codeClient, desc, dateDebut, dateFin, dateDebutR, dateFinR, serviceCible, materielNecessaire, comment;
    private boolean isOpen;
    private LinearLayout clientDetails;
    private AppCompatImageView btn;

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
        codeClient = findViewById(R.id.txtCodeClient);
        desc = findViewById(R.id.txtDescription);
        dateDebut = findViewById(R.id.txtDateDebutPrevue);
        dateDebutR = findViewById(R.id.txtDateDebutReel);

        serviceCible = findViewById(R.id.txtServiceCible);
        materielNecessaire = findViewById(R.id.txtMaterielNecessaire);
        comment = findViewById(R.id.txtCommentaire);

        codeClient.setText(intervention.getClientId());
        desc.setText(intervention.getDescription());

        String prevue = "Prévue : "+MyTools.formatDateFr(intervention.getDateDebutPrevue())+" - "+MyTools.formatDateFr(intervention.getDateFinPrevue());
        String reelle = "Réelle : "+MyTools.formatDateFr(intervention.getDateDebutPrevue())+" - "+MyTools.formatDateFr(intervention.getDateFinPrevue());
        dateDebut.setText(prevue);
        dateDebutR.setText(reelle);

        serviceCible.setText(intervention.getServiceEquipCible());
        comment.setText(intervention.getCommentaire());
        materielNecessaire.setText(intervention.getMaterielNecessaire());

        //TODO..
        //InitAffectations();

        isOpen=false;
        clientDetails = findViewById(R.id.clientDetails);
        //clientDetails.setVisibility(View.GONE);

        btn = (AppCompatImageView)findViewById(R.id.btn_client_detail);
        btn.setOnClickListener(
                v-> onSlideDetails(clientDetails)
        );
    }

    private void InitAffectations(){
       /* tech_list = findViewById(R.id.tech_list);
        tech_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2);
        tech_list.setLayoutManager(layoutManager2);
        SelectedUsersAdapter adapter = new SelectedUsersAdapter(
                selected,
                null,
                true
        );
        tech_list.setAdapter(adapter);*/

        /*mainViewModel.getSelected().observe(getActivity(), selected -> {
            adapter.refresh(selected);
        });*/
    }

    private void onSlideDetails(View view){
        int currentHeight = isOpen? 0 : 800;
        int newHeight = isOpen? 800 : 0;

        Toast.makeText(this, isOpen+" => "+currentHeight+" : "+newHeight, Toast.LENGTH_SHORT).show();
        ValueAnimator slideAnimator = new ValueAnimator()
                .ofInt(currentHeight, newHeight)
                .setDuration(500);

        slideAnimator.addUpdateListener( v-> {
            int value = (int) v.getAnimatedValue();
            Log.i("tests", "onSlideDetails: "+value);
            view.getLayoutParams().height = value;
            view.requestLayout();
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        isOpen = !isOpen;
    }

}