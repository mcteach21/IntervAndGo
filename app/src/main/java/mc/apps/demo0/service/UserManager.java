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

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.AddItemActivity;
import mc.apps.demo0.AdminActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class UserManager {
    private static final String TAG = "tests";
    private static int CPT = 1; // TODO : Cpteur code Intervention

    private MainViewModel mainViewModel;
    private Activity activity;
    public UserManager(Activity activity){
        this.activity = activity;
        mainViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(MainViewModel.class);
    }

    EditText codeUser, firstName, lastName, email, password;
    Spinner profils;
    int profil;
    private void getForm(View root) {
        codeUser = root.findViewById(R.id.txtCodeUser);

        firstName = root.findViewById(R.id.edtFirstname);
        lastName = root.findViewById(R.id.edtLastname);
        email = root.findViewById(R.id.edtlogin);
        password = root.findViewById(R.id.edtpassword);
        profils = root.findViewById(R.id.spinnerProfil);
        profil = profils.getSelectedItemPosition() + 1;
    }

    private boolean checkForm(View root) {
        getForm(root);
        return !(codeUser.getText().toString().isEmpty() || email.getText().toString().isEmpty()
                || firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()
                || password.getText().toString().isEmpty());
    }
    private void setCurrentUser(User user) {
        codeUser.setText(user.getCode());
        firstName.setText(user.getFirstname());
        lastName.setText(user.getLastname());
        email.setText(user.getEmail());
        password.setText(user.getPassword());
        profils.setSelection(user.getProfilId()-1);
    }

    public void prepareAddUser(View root, Class<?> backActivity) {
        getForm(root);

        Button btnadd = root.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(view -> {
                if(addUser(root)) {
                    Intent intent = new Intent(root.getContext(), backActivity);
                    intent.putExtra("num", 1);
                    root.getContext().startActivity(intent);
                }else{
                    Toast.makeText(activity, "Tous les champs sont obligatoires!", Toast.LENGTH_SHORT).show();
                }
            });
    }
    public void prepareUpdateUser(User user, View root, Class<?> backActivity) {
        getForm(root);
        codeUser.setVisibility(View.GONE);
        setCurrentUser(user);

        Button btnadd = root.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(view -> {
            if(updateUser(root)) {
                Intent intent = new Intent(root.getContext(), backActivity);
                intent.putExtra("num", 1);
                root.getContext().startActivity(intent);
            }else{
                Toast.makeText(activity, "Tous les champs sont obligatoires!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private boolean updateUser(View root) {
        return actionOnUser(root, "update");
    }

    private boolean actionOnUser(View root, String action) {
        boolean ok = checkForm(root);
        if(!ok)
            return false;

        User user = new User(
                codeUser.getText().toString(),
                email.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                password.getText().toString(),
                (byte) profil,
                1);

        UserDao dao = new UserDao();
        if(action.equals("add")){
            dao.add(user, (items, message) -> {
                Toast.makeText(root.getContext(), "Utilisateur ajouté avec succès!", Toast.LENGTH_LONG).show();
            });
            resetFields(root);
        }else {
            dao.update(user, (items, message) -> {
                Toast.makeText(root.getContext(), "Utilisateur modifié avec succès!", Toast.LENGTH_LONG).show();
            });
        }
        return true;
    }

    private boolean addUser(View root) {
        return actionOnUser(root, "add");
        /*boolean ok = checkForm(root);
        if(!ok)
            return false;

        User user = new User(
                codeUser.getText().toString(),
                email.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                password.getText().toString(),
                (byte) profil,
                1);

        UserDao dao = new UserDao();
        dao.add(user, (items, message) -> {
            Log.i(TAG, "onCreate: "+message);
            Toast.makeText(root.getContext(), "Utilisateur ajouté avec succès!", Toast.LENGTH_LONG).show();

        });
        resetFields(root);
        return true;*/
    }

    private void resetFields(View root) {
        codeUser.getText().clear();
        firstName.getText().clear();
        lastName.getText().clear();
        email.getText().clear();
        password.getText().clear();
    }



}
