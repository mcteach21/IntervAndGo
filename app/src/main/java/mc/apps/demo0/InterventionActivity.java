package mc.apps.demo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.dao.AdressDao;
import mc.apps.demo0.dao.AffectationDao;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;

public class InterventionActivity extends AppCompatActivity {

    private static final String TAG = "tests";
    Intervention intervention;
    TextView codeClient, desc, supervisor, dateDebut, dateFin, dateDebutR, dateFinR, serviceCible, materielNecessaire, comment;
    private boolean isOpen;
    private ConstraintLayout clientDetails;
    private AppCompatImageView btn, btn_maps;
    private boolean goto_rapport;
    private Button btn_start, btn_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervention);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Intervention");

        Intent intent = getIntent();
        intervention = (Intervention) intent.getSerializableExtra("intervention");

        if(intervention==null){
            finish();
        }

        goto_rapport = intent.getBooleanExtra("rapport", false);
        Init();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(goto_rapport)
            getMenuInflater().inflate(R.menu.menu_interv, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
            finish();

        //MASQUE!
        /*if (id==R.id.item_menu_rapport){
            Intent data =  new Intent();
            data.putExtra("interv", intervention);
            setResult(RESULT_OK, data);
            finish();
        }*/

        return false;
    }

    @SuppressLint("WrongViewCast")
    private void Init() {

        codeClient = findViewById(R.id.txtCodeClient);
        desc = findViewById(R.id.txtDescription);
        supervisor = findViewById(R.id.txtSupervisor);
        dateDebut = findViewById(R.id.txtDatePrevue);
        dateDebutR = findViewById(R.id.txtDateDebutReel);

        serviceCible = findViewById(R.id.txtServiceCible);
        materielNecessaire = findViewById(R.id.txtMaterielNecessaire);
        comment = findViewById(R.id.txtCommentaire);

        TextView title = findViewById(R.id.fragment_title);
        String statut="en attente";
        switch(intervention.getStatutId()){
            case 2:statut="en cours";break;
            case 3:statut="abandonnée";break;
            case 4:statut="à poursuivre";break;
            case 5:statut="terminée";break;
        }

        title.setText(intervention.getCode()+" ["+statut+"]");

        codeClient.setText( "Client : "+intervention.getClientId());
        desc.setText("Description Intervention : \n"+intervention.getDescription());

        UserDao udao = new UserDao();
        udao.findByCode(intervention.getSuperviseurId(), (items_, mess_)->{
            List<User> users = udao.Deserialize(items_, User.class);
            if(!users.isEmpty())
                supervisor.setText("Superviseur : \n"+users.get(0).getFirstname()+" "+users.get(0).getLastname());
        });

        String prevue = "Date Intervention Prévue \n"+MyTools.formatDateFr(intervention.getDateDebutPrevue())+" - "+MyTools.formatDateFr(intervention.getDateFinPrevue());
        String reelle = "Date Intervention Réelle \n";

        if(intervention.getDateDebutReelle()!=null)
            reelle += MyTools.formatDateFr(intervention.getDateDebutReelle());
        if(intervention.getDateFinReelle()!=null)
            reelle += " - "+MyTools.formatDateFr(intervention.getDateFinReelle());

        dateDebut.setText(prevue);
        dateDebutR.setText(reelle);

        serviceCible.setText("Service/Equipement ciblé : \n"+intervention.getServiceEquipCible());
        comment.setText("Consignes : \n"+intervention.getConsignes()+"\n\n"+"Observations : \n"+intervention.getObservations());
        materielNecessaire.setText("Matériel nécessaire : \n"+intervention.getMaterielNecessaire());

        InitAffectations();

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
                    nomClient.setText("Nom : "+client.getNom());
                    infosClient.setText("Contact : "+client.getContact()+"\n Tél. : "+client.getTelephone()+"\n Email : "+client.getEmail());

                    setClientAdress(adressClient, client);
                }
            });
        } catch (UnsupportedEncodingException e) {}

        isOpen=false;
        clientDetails = findViewById(R.id.clientDetails);
        clientDetails.getLayoutParams().height = 0;

        btn = findViewById(R.id.btn_client_detail);
        btn.setOnClickListener(
                v-> onSlideDetails(clientDetails)
        );

        btn_maps = findViewById(R.id.btn_maps);

        btn_start = findViewById(R.id.btn_start_interv);
        btn_finish = findViewById(R.id.btn_finish_interv);

        btn_start.setVisibility(intervention.getStatutId()==5?View.GONE:View.VISIBLE);
        btn_finish.setVisibility(intervention.getStatutId()==5?View.GONE:View.VISIBLE);

        btn_start.setEnabled(intervention.getStatutId()==1);
        if(intervention.getStatutId()!=1)
            btn_start.setTextColor(Color.WHITE);

        btn_start.setOnClickListener(v->updateIntervention(true));
        btn_finish.setOnClickListener(v->updateIntervention(false));
    }

    private void updateIntervention(boolean start) {
        String now =  MyTools.getCurrentDate();
        if(start) {
            intervention.setDateDebutReelle(now);

            intervention.setStatutId(2);
            updateAndExit(start);
        }else {
            showObservationsDialog();
        }

    }

    private void updateAndExit(boolean start) {
        InterventionDao dao = new InterventionDao();
        dao.update(intervention, (items, mess)->{
            Toast.makeText(this, "Intervention "+(start?"Commencée!":"Terminée!"), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, null);
            finish();
        });
    }

    public void showObservationsDialog() {
        String now =  MyTools.getCurrentDate();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Observations");

        final View customLayout = getLayoutInflater().inflate(R.layout.rapport_input_layout, null);
        builder.setView(customLayout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            EditText editText = customLayout.findViewById(R.id.edtIntervRapport);
            String observations = editText.getText().toString();

            intervention.setStatutId(5);
            intervention.setDateFinReelle(now);
            intervention.setObservations(observations);

            updateAndExit(false);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setClientAdress(TextView txtAdress, Client client) {
        AdressDao dao = new AdressDao();
        dao.ofClient(client.getCode(), (items, message) -> {
            List<Adress> adresses = dao.Deserialize(items, Adress.class);
            if(adresses.size()>0) {
                txtAdress.setText(adresses.get(0).getVoie() + "\n" + adresses.get(0).getCp() + " " + adresses.get(0).getVille());

                String adress = adresses.get(0).getVoie()+" "+adresses.get(0).getCp()+ " " + adresses.get(0).getVille();
                btn_maps.setOnClickListener(
                        v-> showAdressInMaps(adress)
                );
            }
        });
    }
    private void showAdressInMaps(String adress) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("adress", adress);
        startActivity(intent);
    }
    private void onSlideDetails(View view){
        int currentHeight = isOpen?900:0;
        int newHeight = isOpen?0:900;
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

    RecyclerView tech_list;
    List<User> technicians = new ArrayList();

    private void InitAffectations(){
        AffectationDao dao = new AffectationDao();
        dao.find(intervention.getCode(), (items, message)->{
            List<User> users =  new UserDao().Deserialize(items, User.class);
            Log.i(TAG, "Affectations - Users : "+users);

            technicians.clear();
            technicians.addAll(users);
            tech_list.getAdapter().notifyDataSetChanged();
        });

        tech_list = findViewById(R.id.list_techs);
        tech_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2);
        tech_list.setLayoutManager(layoutManager2);

        SelectedUsersAdapter adapter = new SelectedUsersAdapter(
                technicians,
                null,
                false
        );
        tech_list.setAdapter(adapter);
    }

}