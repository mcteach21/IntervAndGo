package mc.apps.interv.service;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.List;

import mc.apps.interv.R;
import mc.apps.interv.dao.AdressDao;
import mc.apps.interv.dao.ClientDao;
import mc.apps.interv.dao.ContratDao;
import mc.apps.interv.model.Adress;
import mc.apps.interv.model.Client;
import mc.apps.interv.model.Contrat;
import mc.apps.interv.viewmodels.MainViewModel;

public class ClientManager {
    private static final String TAG = "tests";
    private MainViewModel mainViewModel;
    private Activity activity;

    public ClientManager(Activity activity){
        this.activity = activity;
        mainViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(MainViewModel.class);
    }

    private void getForm(View root) {
        code = root.findViewById(R.id.txtCodeClient);
        nom = root.findViewById(R.id.edtNomClient);
        contrat = root.findViewById(R.id.edtContratClient);
        contact = root.findViewById(R.id.edtNomContact);
        email = root.findViewById(R.id.edtEmail);
        tel = root.findViewById(R.id.edtTelephone);
        voie = root.findViewById(R.id.edtAdresseClient);
        cp = root.findViewById(R.id.edtCpClient);
        ville = root.findViewById(R.id.edtVilleClient);
    }
    private void setCurrentClient(Client client) {
        code.setText(client.getCode());
        nom.setText(client.getNom());
        contact.setText(client.getContact());
        email.setText(client.getEmail());
        tel.setText(client.getTelephone());

        AdressDao dao1 = new AdressDao();
        dao1.ofClient(client.getCode(), (items, message) -> {
            List<Adress> adresses = dao1.Deserialize(items, Adress.class);
            if(adresses.size()>0){
                voie.setText(adresses.get(0).getVoie());
                cp.setText(adresses.get(0).getCp());
                ville.setText(adresses.get(0).getVille());
            }
        });
        ContratDao dao2 = new ContratDao();
        dao2.ofClient(client.getCode(), (items, message) -> {
            List<Contrat> contrats = dao2.Deserialize(items, Contrat.class);
            if(contrats.size()>0)
                contrat.setText(contrats.get(0).getCode());
        });
    }

    private boolean checkForm(View root) {
        getForm(root);
        return !(code.getText().toString().isEmpty() || nom.getText().toString().isEmpty() || email.getText().toString().isEmpty()
                || contrat.getText().toString().isEmpty() || contact.getText().toString().isEmpty()
                || tel.getText().toString().isEmpty() || voie.getText().toString().isEmpty()
                || cp.getText().toString().isEmpty() || ville.getText().toString().isEmpty());
    }

    public void prepareAddClient(View root, Class<?> backActivity) {
        getForm(root);

        Button btnadd = root.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(view -> {
                if(addClient(root)) {
                    Intent intent = new Intent(root.getContext(), backActivity);
                    intent.putExtra("num",3);
                    root.getContext().startActivity(intent);
                }else{
                    Toast.makeText(activity, "Tous les champs sont obligatoires!", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void prepareUpdateClient(Client client, View root, Class<?> backActivity) {
        getForm(root);
        code.setVisibility(View.GONE);
        setCurrentClient(client);

        Button btnadd = root.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(view -> {
            if(updateClient(root)) {
                Intent intent = new Intent(root.getContext(), backActivity);
                intent.putExtra("num",3);
                root.getContext().startActivity(intent);
            }else{
                Toast.makeText(activity, "Tous les champs sont obligatoires!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean updateClient(View root) {
        return actionOnClient(root,"update");
    }

    EditText code, nom, contrat, contact, email, tel, voie, cp, ville;

    private boolean addClient(View root) {
        return actionOnClient(root,"add");
    }

    private boolean actionOnClient(View root, String action) {
        boolean ok = checkForm(root);
        if(!ok)
            return false;

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



        ClientDao dao = new ClientDao();
        if(action.equals("add")) {

            Adress adress = new Adress(0, "principale" ,
                    voie.getText().toString() , cp.getText().toString(),
                    ville.getText().toString(), code.getText().toString());

            //TODO : code contrat..
            Contrat contrat_client = new Contrat("C"+code.getText().toString(), contrat.getText().toString(), code.getText().toString());

            dao.add(client, (items, message) -> {
                Log.i(TAG, "onCreate: " + message);
                Toast.makeText(root.getContext(), "Client ajouté avec succès!", Toast.LENGTH_LONG).show();

                AdressDao dao2 = new AdressDao();
                dao2.add(adress, (items2, message2) -> {
                    Log.i(TAG, "onCreate: " + message2);
                });

                ContratDao dao3 = new ContratDao();
                dao3.add(contrat_client, (items3, message3) -> {
                    Log.i(TAG, "onCreate: " + message3);
                });
            });
        }else{
            dao.update(client, (items, message) -> {
                Log.i(TAG, "onCreate: " + message);
                Toast.makeText(root.getContext(), "Client modifié avec succès!", Toast.LENGTH_LONG).show();

                //TODO : contrat..adress..
               /* AdressDao dao2 = new AdressDao();
                dao2.update(adress, (items2, message2) -> {
                    Log.i(TAG, "onCreate: " + message2);
                });

                ContratDao dao3 = new ContratDao();
                dao3.update(contrat_client, (items3, message3) -> {
                    Log.i(TAG, "onCreate: " + message3);
                });*/
            });
        }

        resetFields(root);
        return true;
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
