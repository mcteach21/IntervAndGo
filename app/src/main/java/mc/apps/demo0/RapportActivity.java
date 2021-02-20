package mc.apps.demo0;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
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
import mc.apps.demo0.dao.FileDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.libs.PDFUtil;
import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Contrat;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.InterventionFile;
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
        PDFUtil.createPdfFromContent(RapportActivity.this, contents, signatures, photos, fileName);
    }


    List<Bitmap> signatures = new ArrayList();
    List<Bitmap> photos = new ArrayList();

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

        FileDao fdao = new FileDao();
        fdao.find(intervention.getCode(), (data,m)->{
            List<InterventionFile> items = fdao.Deserialize(data, InterventionFile.class);
            String filename;
            URL url;
            Bitmap bmp;
            if(items.size()>0){
                for (InterventionFile item:items ) {
                    filename = item.getFilename();
                    try {
                        url = new URL("https://mc69website.000webhostapp.com/uploads/"+filename);
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        //imageView.setImageBitmap(bmp);

                        Log.i(TAG, "Init: bitmap = "+bmp);
                        if(item.getPhoto()==0)
                            signatures.add(bmp);
                        else
                            photos.add(bmp);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                createImagesGrid();
            }
        });


        ClientDao dao = new ClientDao();
        dao.find(intervention.getClientId(), (data, message) -> {
            List<Client> items = dao.Deserialize(data, Client.class);
            if(items.size() >0){
                Client client = items.get(0);
                txtRapport1.setText(txtRapport1.getText()+" "+client.getNom());
                setClientAdress(txtRapport1, client);
            }
        });

        txtRapport2.setText(MyTools.formatDateFr(intervention.getDateDebutReelle())+" - "+MyTools.formatDateFr(intervention.getDateFinReelle())+"\n"+intervention.getDescription());
        txtRapport3.setText(intervention.getConsignes()+"\n\n"+intervention.getObservations());

        txtRapport4.setText("Technicien(s) :");
        AffectationDao adao = new AffectationDao();
        adao.find(intervention.getCode(), (items, message)->{
            List<User> users =  new UserDao().Deserialize(items, User.class);
            for(User user : users)
                txtRapport4.setText(txtRapport4.getText()+"\n- "+user.getFirstname()+" "+user.getLastname());
        });
    }

    private void createImagesGrid() {
        int total = signatures.size()+photos.size();
        Log.i(TAG, "createImagesGrid: total = "+total);

        ImageView imageView;
        int i=0;
        int margin = 6;
        androidx.gridlayout.widget.GridLayout grid = findViewById(R.id.grid_images);
        Button btn;
        final float scale =  getResources().getDisplayMetrics().density;
        for (Bitmap bmp: signatures) {
            imageView = new ImageView(getApplicationContext());
            imageView.setImageBitmap(bmp);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (120 * scale + 0.5f), (int) (120 * scale + 0.5f)
            );
            params.setMargins(margin, margin, margin, margin);
            imageView.setLayoutParams(params);

            grid.addView(imageView);
        }
        for (Bitmap bmp: photos) {
            imageView = new ImageView(getApplicationContext());
            imageView.setImageBitmap(bmp);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (100 * scale + 0.5f), (int) (100 * scale + 0.5f)
            );
            params.setMargins(margin, margin, margin, margin);
            imageView.setLayoutParams(params);

            grid.addView(imageView);
        }
        grid.setUseDefaultMargins(true);

    }

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