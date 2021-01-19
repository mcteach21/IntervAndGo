package mc.apps.demo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.Dao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;

public class InterventionActivity extends AppCompatActivity {

    private static final String TAG = "tests";
    Intervention intervention;
    TextView codeClient, desc, dateDebut, dateFin, dateDebutR, dateFinR, serviceCible, materielNecessaire, comment;
    private boolean isOpen;
    private LinearLayout clientDetails;
    private AppCompatImageView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervention);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Intervention (Détails)");

        Intent intent = getIntent();
        intervention = (Intervention) intent.getSerializableExtra("intervention");
        if(intervention==null){
            finish();
        }

        //TextView title = findViewById(R.id.layout_title);
        Init();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
            finish();

        return false;
    }

    @SuppressLint("WrongViewCast")
    private void Init() {
        codeClient = findViewById(R.id.txtCodeClient);
        desc = findViewById(R.id.txtDescription);
        dateDebut = findViewById(R.id.txtDateDebutPrevue);
        dateDebutR = findViewById(R.id.txtDateDebutReel);

        serviceCible = findViewById(R.id.txtServiceCible);
        materielNecessaire = findViewById(R.id.txtMaterielNecessaire);
        comment = findViewById(R.id.txtCommentaire);

        codeClient.setText( "Client : "+intervention.getClientId());
        desc.setText(intervention.getDescription());

        String prevue = "Date Intervention Prévue \n"+MyTools.formatDateFr(intervention.getDateDebutPrevue())+" - "+MyTools.formatDateFr(intervention.getDateFinPrevue());
        String reelle = "Date Intervention Réelle \n";
        if(intervention.getDateDebutReelle()!=null)
            reelle += MyTools.formatDateFr(intervention.getDateDebutReelle());
        if(intervention.getDateFinReelle()!=null)
            reelle += " - "+MyTools.formatDateFr(intervention.getDateFinReelle());

        dateDebut.setText(prevue);
        dateDebutR.setText(reelle);

        serviceCible.setText(intervention.getServiceEquipCible());
        comment.setText(intervention.getCommentaire());
        materielNecessaire.setText(intervention.getMaterielNecessaire());

        //TODO..
        //InitAffectations();

        /**
         * Infos Client
         */
        TextView nomClient =  findViewById(R.id.txtNomClient);
        TextView infosClient =  findViewById(R.id.txtInfosClient);
        TextView adressClient =  findViewById(R.id.txtAdressClient);

        ClientDao dao = new ClientDao();

        String code_client = null;
        try {
            code_client = URLEncoder.encode(intervention.getClientId(), "utf-8");
            dao.find(code_client, (data, message) -> {
                List<Client> items = dao.Deserialize(data, Client.class);

                if(items.size() >0){
                    Client client = items.get(0);
                    nomClient.setText(client.getNom());
                    infosClient.setText("Contact : "+client.getContact()+"\n Tél. : "+client.getTelephone()+"\n Email : "+client.getEmail());
                    adressClient.setText("adress client..");
                }
            });
        } catch (UnsupportedEncodingException e) {}

        isOpen=false;
        clientDetails = findViewById(R.id.clientDetails);
        clientDetails.getLayoutParams().height = 0;

        btn = (AppCompatImageView) findViewById(R.id.btn_client_detail);
        btn.setOnClickListener(
                v-> onSlideDetails(clientDetails)
        );
    }

    private void onSlideDetails(View view){
        int currentHeight = isOpen?800:0;
        int newHeight = isOpen?0:800;
        ValueAnimator slideAnimator = new ValueAnimator()
                .ofInt(currentHeight, newHeight)
                .setDuration(500);

        slideAnimator.addUpdateListener( v-> {
            int value = (int) v.getAnimatedValue();
            view.getLayoutParams().height = value;
            view.requestLayout();
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        isOpen = !isOpen;
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

}