package mc.apps.demo0.ui.item;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import mc.apps.demo0.ItemActivity;
import mc.apps.demo0.MapsActivity;
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

public class ItemFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "tests";
    private MainViewModel mainViewModel;
    private View root;
    private TextView title, infos, adress;
    private Button btnDelete, btnUpdate;
    private ImageView btnMaps;

    private Serializable currentItem;

    public static ItemFragment newInstance() {
        return new ItemFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.item_main_fragment, container, false);
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        mainViewModel.getItem().observe(getActivity(), item->{
            currentItem = item;
            handleItem();
        });
        super.onAttach(context);
    }

    private void handleItem() {

        title = root.findViewById(R.id.item_title);
        infos = root.findViewById(R.id.item_infos);
        adress = root.findViewById(R.id.item_adress);

        title.setText(currentItem.getClass().getSimpleName());

        btnDelete = root.findViewById(R.id.item_btn_delete);
        btnUpdate = root.findViewById(R.id.item_btn_update);

        btnMaps = root.findViewById(R.id.btn_maps);

        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        adress.setVisibility(View.GONE);
        btnMaps.setVisibility(View.GONE);

        if(currentItem.getClass().getSimpleName().equals("User")){
            setUser();
        }else  if(currentItem.getClass().getSimpleName().equals("Client")){
            setClient();
        }else{
            //TODO intervention ??
        }
    }

    private void setUser() {
        User compte = (User)currentItem;

        String info = "Code : "+compte.getCode();
        info +="\nNom : "+compte.getFirstname()+" "+compte.getLastname();

        String profil="";
        switch(compte.getProfilId()){
            case 1 : profil="Administrateur"; break;
            case 2 : profil="Superviseur"; break;
            case 3 : profil="Technicien"; break;
        }
        info +="\nEmail : "+compte.getEmail()+"\nProfil : "+profil;
        infos.setText(info);
    }

    private void setClient() {
        Client client = (Client)currentItem;

        String info = "Code : "+client.getCode();
        info +="\nNom : "+client.getNom();
        info +="\nContact : "+client.getContact()+"\nTél. : "+client.getTelephone()+"\nEmail : "+client.getEmail();

        infos.setText(info);

        AdressDao dao1 = new AdressDao();
        dao1.ofClient(client.getCode(), (items, message) -> {
            List<Adress> adresses = dao1.Deserialize(items, Adress.class);
            if(adresses.size()>0) {
                adress.setVisibility(View.VISIBLE);
                btnMaps.setVisibility(View.VISIBLE);

                adress.setText("Adresse : " + adresses.get(0).getVoie() + "\n" + adresses.get(0).getCp() + " " + adresses.get(0).getVille());
                btnMaps.setOnClickListener(v->{
                    showAdressInMaps(adress.getText().toString());
                });
            }
        });

        ContratDao dao2 = new ContratDao();
        dao2.ofClient(client.getCode(), (items, message) -> {
            List<Contrat> contrats = dao2.Deserialize(items, Contrat.class);
            if(contrats.size()>0)
                infos.setText(infos.getText()+"\n\nContrat : "+contrats.get(0).getNom()+" ["+contrats.get(0).getCode()+"]");
        });

    }
    private void showAdressInMaps(String adress) {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra("adress", adress);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        String action = (String) ((Button)view).getText();
        if(view.getId() == R.id.item_btn_delete){
            deleteItem();
        }else{
            updateItem();
        }
    }
    private void deleteItem() {

        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setMessage("Vous allez supprimer ce "+currentItem.getClass().getSimpleName()+" définitivement! Etes-vous sûr?")
                .setPositiveButton("Supprimer", (dialog, which) -> {

                    //TODO : si item utilisé!!

                    if(currentItem.getClass().getSimpleName().equals("User")) {
                        UserDao dao = new UserDao();
                        User compte = (User)currentItem;
                        dao.delete(compte.getCode(),
                                (items, message) -> getActivity().finish()
                        );
                    }else if(currentItem.getClass().getSimpleName().equals("Client")) {
                        ClientDao dao = new ClientDao();
                        Client compte = (Client)currentItem;
                        dao.delete(compte.getCode(),
                                (items, message) -> getActivity().finish()
                        );
                    }else{

                    }
                })
                .setNegativeButton("Annuler", null).show();
    }
    private void updateItem() {
    }


}