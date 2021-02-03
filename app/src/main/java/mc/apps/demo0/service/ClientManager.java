package mc.apps.demo0.service;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import mc.apps.demo0.R;
import mc.apps.demo0.dao.AdressDao;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.ContratDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Contrat;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class ClientManager {
    private static final String TAG = "tests";
    private MainViewModel mainViewModel;
    private Activity activity;

    public ClientManager(Activity activity){
        this.activity = activity;
        mainViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(MainViewModel.class);
    }

    public void prepareAddClient(View root, Class<?> backActivity) {
        Button btnadd = root.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(view -> {
                addClient(root);

                Intent intent = new Intent(root.getContext(), backActivity);
                intent.putExtra("num",3);
                root.getContext().startActivity(intent);
                Log.i(TAG, "prepareAddClient: back to "+backActivity.getSimpleName());
            });
    }

    EditText code, nom, contrat, contact, email, tel, voie, cp, ville;

    private void addClient(View root) {
        code = root.findViewById(R.id.txtCodeClient);
        nom = root.findViewById(R.id.edtNomClient);
        contrat = root.findViewById(R.id.edtContratClient);
        contact = root.findViewById(R.id.edtNomContact);
        email = root.findViewById(R.id.edtEmail);
        tel = root.findViewById(R.id.edtTelephone);
        voie = root.findViewById(R.id.edtAdresseClient);
        cp = root.findViewById(R.id.edtCpClient);
        ville = root.findViewById(R.id.edtVilleClient);

        Client client = new Client(
                code.getText().toString(),
                nom.getText().toString(),
                contact.getText().toString(),
                email.getText().toString(),
                tel.getText().toString(),
                voie.getText().toString(),
                cp.getText().toString(),
                ville.getText().toString(),
                contrat.getText().toString()
        );
        Adress adress = new Adress(0, "principale" ,
                voie.getText().toString() , cp.getText().toString(),
                ville.getText().toString(), code.getText().toString());

        //TODO : code contrat..
        Contrat contrat_client = new Contrat("C"+code.getText().toString(), contrat.getText().toString(), code.getText().toString());


        ClientDao dao = new ClientDao();
        dao.add(client, (items, message) -> {
            Log.i(TAG, "onCreate: "+message);
            Toast.makeText(root.getContext(), "Client ajouté avec succès!", Toast.LENGTH_LONG).show();

            AdressDao dao2 = new AdressDao();
            dao2.add(adress, (items2, message2) -> {
                Log.i(TAG, "onCreate: "+message2);
            });

            ContratDao dao3 = new ContratDao();
            dao3.add(contrat_client, (items3, message3) -> {
                Log.i(TAG, "onCreate: "+message3);
            });
        });


        resetFields(root); //reinitialiser form planfication!
    }

    private void resetFields(View root) {
        code.getText().clear();
        nom.getText().clear();
        contact.getText().clear();
        email.getText().clear();
        tel.getText().clear();
        voie.getText().clear();
        cp.getText().clear();
        ville.getText().clear();
    }
}
