package mc.apps.demo0;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.model.User;

public class PasswordResetActivity extends AppCompatActivity {

    private static final String TAG = "tests";
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        setTitle("Modifier Mot de Passe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        if(user==null)
            finish();

    }

    public void UpdatePassword(View view) {
       EditText password0 = findViewById(R.id.edtTempPassword);
       EditText password1 = findViewById(R.id.edtNewPassword);
       EditText password2 = findViewById(R.id.edtConfirmPassword);

       if(user.getPassword().equals(password0.getText().toString())){
           if(!password1.getText().toString().isEmpty() && password1.getText().toString().equals(password2.getText().toString())){
               user.setPassword(password1.getText().toString());
               user.setActivated(1);
               UpdateUserPassword(user);
           }else{
               Toast.makeText(this, "les 2 mots de passe ne correspondent pas!", Toast.LENGTH_SHORT).show();
               password1.requestFocus();
           }
       }else{
           Toast.makeText(this, "Mot de passe temporaire non valide!", Toast.LENGTH_SHORT).show();
           password0.requestFocus();
       }
    }
    private void UpdateUserPassword(User u) {
        UserDao dao = new UserDao();
        dao.update(u, (items, message)->{
            Toast.makeText(this, "Mot de passe modifié avec succès!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
            finish();
        return false;
    }
}