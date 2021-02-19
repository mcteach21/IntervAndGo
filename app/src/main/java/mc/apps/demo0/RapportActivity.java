package mc.apps.demo0;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mc.apps.demo0.dao.AdressDao;
import mc.apps.demo0.dao.AffectationDao;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.ContratDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.libs.PDFUtil;
import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Contrat;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;


public class RapportActivity extends AppCompatActivity {
    private static final String TAG = "tests";

    Intervention intervention;
    TextView txtRapport1, txtRapport2, txtRapport3, txtRapport4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapport);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Rapport Intervention");

        Intent intent = getIntent();
        intervention = (Intervention) intent.getSerializableExtra("intervention");
        if(intervention==null){
            finish();
        }

        Init();
    }

    public void generate(View v){
        verifyStoragePermissions(this);
        GeneratePDF();
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void GeneratePDF() {

        //VM ignores the file URI exposure!
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String currentDate = new SimpleDateFormat("ddMMyyHHmm", Locale.getDefault()).format(new Date());
        Log.i("tests" , "onCreate: "+currentDate);
        String fileName = "rapport_"+ currentDate;

        String[] contents = { (String) txtRapport1.getText(), (String) txtRapport2.getText(), (String) txtRapport3.getText(), (String) txtRapport4.getText()};
        PDFUtil.createPdfFromContent(RapportActivity.this, contents, fileName);

        /*PDFUtil pdf = PDFUtil.getInstance();

        View view = getLayoutInflater().inflate(R.layout.activity_rapport, null);
        List<View> views = new ArrayList<View>();
        views.add(view);

        pdf.generatePDF(getApplicationContext(), views, fileName, new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(RapportActivity.this, "pdf Generation Success!", Toast.LENGTH_SHORT).show();
                PDFUtil.OpenPDF(getApplicationContext(), savedPDFFile);
            }
            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(RapportActivity.this, "pdf Generation Failure : "+exception, Toast.LENGTH_SHORT).show();
            }
        });*/
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

        txtRapport1 = findViewById(R.id.txtRapport1);
        txtRapport2  = findViewById(R.id.txtRapport2);
        txtRapport3 =  findViewById(R.id.txtRapport3);
        txtRapport4 =  findViewById(R.id.txtRapport4);

        txtRapport1.setText( "Client : "+intervention.getClientId());


        ClientDao dao = new ClientDao();
        dao.find(intervention.getClientId(), (data, message) -> {
            List<Client> items = dao.Deserialize(data, Client.class);
            if(items.size() >0){
                Client client = items.get(0);
                txtRapport1.setText(txtRapport1.getText()+" "+client.getNom());
                setClientAdress(txtRapport1, client);
            }
        });

        txtRapport2.setText(intervention.getDateDebutReelle()+" - "+intervention.getDateFinReelle()+"\n"+intervention.getDescription());
        txtRapport3.setText(intervention.getConsignes()+"\n\n"+intervention.getObservations());

        txtRapport4.setText("Technicien(s) :");
        AffectationDao adao = new AffectationDao();
        adao.find(intervention.getCode(), (items, message)->{
            List<User> users =  new UserDao().Deserialize(items, User.class);
            for(User user : users)
                txtRapport4.setText(txtRapport4.getText()+"\n- "+user.getFirstname()+" "+user.getLastname());
        });
    }
    /*private void showAdressInMaps(String adress) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("adress", adress);
        startActivity(intent);
    }*/

    private void setClientAdress(TextView txtClient, Client client) {
        AdressDao dao = new AdressDao();
        dao.ofClient(client.getCode(), (items, message) -> {
            List<Adress> adresses = dao.Deserialize(items, Adress.class);
            if(adresses.size()>0) {
                txtClient.setText(txtClient.getText()+"\n"+adresses.get(0).getVoie() + "\n" + adresses.get(0).getCp() + " " + adresses.get(0).getVille());
                txtClient.setText(txtClient.getText()+"\nTél. : "+client.getTelephone()+"\nEmail : "+client.getEmail());
            }
        });
    }
}