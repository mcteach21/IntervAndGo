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

    public void prepareAddUser(View root, Class<?> backActivity) {
        Button btnadd = root.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(view -> {
                addUser(root);

                Intent intent = new Intent(root.getContext(), backActivity);
                intent.putExtra("num",1);
                root.getContext().startActivity(intent);
                Log.i(TAG, "prepareAddUser: back to "+backActivity.getSimpleName());
            });
    }

    EditText codeUser, firstName, lastName, email, password;
    int profil;
    private void addUser(View root) {
        codeUser = root.findViewById(R.id.txtCodeUser);
        firstName = root.findViewById(R.id.edtFirstname);
        lastName = root.findViewById(R.id.edtLastname);
        email = root.findViewById(R.id.edtlogin);
        password = root.findViewById(R.id.edtpassword);
        Spinner profils = root.findViewById(R.id.spinnerProfil);
        profil = profils.getSelectedItemPosition()+1;

        User user = new User(
                codeUser.getText().toString(),
                email.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                password.getText().toString(),
                (byte) profil);

        UserDao dao = new UserDao();
        dao.add(user, (items, message) -> {
            Log.i(TAG, "onCreate: "+message);
            Toast.makeText(root.getContext(), "Utilisateur ajouté avec succès!", Toast.LENGTH_LONG).show();
        });
        resetFields(root); //reinitialiser form planfication!
    }

    private void resetFields(View root) {
        codeUser.getText().clear();
        firstName.getText().clear();
        lastName.getText().clear();
        email.getText().clear();
        password.getText().clear();
    }


}
