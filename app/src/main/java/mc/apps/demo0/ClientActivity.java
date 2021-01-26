package mc.apps.demo0;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import mc.apps.demo0.dao.AdressDao;
import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;


public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "tests";

    Client client;
    TextView codeClient, nomClient, infosClient, adressClient;

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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_interv, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

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


        codeClient.setText( "Code : "+client.getCode());
        nomClient.setText( "Nom : "+client.getNom());

        infosClient.setText("Contact : "+client.getContact()+"\n Tél. : "+client.getTelephone()+"\n Email : "+client.getEmail());

        setClientAdress(adressClient, client);
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

}