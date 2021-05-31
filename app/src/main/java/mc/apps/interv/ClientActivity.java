package mc.apps.interv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import mc.apps.interv.dao.AdressDao;
import mc.apps.interv.dao.ContratDao;
import mc.apps.interv.model.Adress;
import mc.apps.interv.model.Client;
import mc.apps.interv.model.Contrat;


public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "tests";

    Client client;
    TextView codeClient, nomClient, infosClient, adressClient, contratClient;
    ImageView btn_maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Client");

        Intent intent = getIntent();
        client = (Client) intent.getSerializableExtra("client");
        if(client==null)
            finish();

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
        nomClient  = findViewById(R.id.txtNomClient);
        infosClient =  findViewById(R.id.txtInfosClient);
        adressClient =  findViewById(R.id.txtAdressClient);

        contratClient =  findViewById(R.id.txtContratClient);
        btn_maps = findViewById(R.id.btn_maps);
        btn_maps.setOnClickListener(v->{
            if(!adressClient.getText().toString().isEmpty())
                showAdressInMaps(adressClient.getText().toString());
        });

        codeClient.setText( "Code : "+client.getCode());
        nomClient.setText( "Nom : "+client.getNom());

        infosClient.setText("Contact : "+client.getContact()+"\n Tél. : "+client.getTelephone()+"\n Email : "+client.getEmail());

        setClientAdress(adressClient, client);
        setClientContrat(contratClient, client);
    }
    private void showAdressInMaps(String adress) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("adress", adress);
        startActivity(intent);
    }

    private void setClientAdress(TextView txtAdress, Client client) {
        AdressDao dao = new AdressDao();
        dao.ofClient(client.getCode(), (items, message) -> {
            List<Adress> adresses = dao.Deserialize(items, Adress.class);
            if(adresses.size()>0)
                txtAdress.setText(adresses.get(0).getVoie()+"\n"
                        +adresses.get(0).getCp()+" "+adresses.get(0).getVille());
        });
    }
    private void setClientContrat(TextView txtContrat, Client client) {
        ContratDao dao = new ContratDao();
        dao.ofClient(client.getCode(), (items, message) -> {
            List<Contrat> contrats = dao.Deserialize(items, Contrat.class);
            if(contrats.size()>0)
                txtContrat.setText(contrats.get(0).getNom()+" ["+contrats.get(0).getCode()+"]");
        });
    }
}