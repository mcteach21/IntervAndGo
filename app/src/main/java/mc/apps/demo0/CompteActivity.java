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
import mc.apps.demo0.model.User;


public class CompteActivity extends AppCompatActivity {
    private static final String TAG = "tests";

    User compte;
    TextView codeCompte, nomCompte, infosCompte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Compte");

        Intent intent = getIntent();
        compte = (User) intent.getSerializableExtra("compte");
        if(compte==null)
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

    private void Init() {
        codeCompte = findViewById(R.id.txtCodeCompte);
        nomCompte  = findViewById(R.id.txtNomCompte);
        infosCompte =  findViewById(R.id.txtInfosCompte);

        codeCompte.setText( "Code : "+compte.getCode());
        nomCompte.setText( "Nom : "+compte.getLastname()+" "+compte.getLastname());

        String profil="";
        switch(compte.getProfilId()){
            case 1 : profil="Administrateur"; break;
            case 2 : profil="Superviseur"; break;
            case 3 : profil="Technicien"; break;
        }
        infosCompte.setText("Email : "+compte.getEmail()+"\n Profil : "+profil);
    }

}