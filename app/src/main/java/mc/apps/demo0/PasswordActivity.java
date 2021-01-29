package mc.apps.demo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.User;

public class PasswordActivity extends AppCompatActivity {

    private static final String TAG = "tests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        setTitle("Mot de passe oublié");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void ResetPassword(View view) {
        EditText email = findViewById(R.id.edtlogin);
        if(!email.getText().toString().isEmpty()) {

            UserDao dao = new UserDao();
            dao.findByLogin(email.getText().toString(), (items, message)->{
                List<User> users = dao.Deserialize(items, User.class);
                Log.i(TAG, "ResetPassword: "+users);
                if(users.size()>0) {
                    User u = users.get(0);

                    //envoi mail avec mot de passe temporaire
                    String generated_password = MyTools.GetRandomPassword();
                    MyTools.SendPasswordMail(u.getFirstname() , email.getText().toString(), generated_password);

                    //mise à jour mot de passe dans base de données
                    u.setPassword(generated_password);
                    UpdateUserPassword(u);

                    ((TextView) findViewById(R.id.reset_how_txt)).setText("Un email pour réinitiliser votre mot de passe est envoyé à l'adresse mail de votre compte.");
                    findViewById(R.id.textInputLogin).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btnResetPassword).setVisibility(View.INVISIBLE);
                }else{
                    ((TextView) findViewById(R.id.reset_how_txt)).setText("Adresse mail non reconnue! Veuillez ressaisir.");
                    email.requestFocus();
                }
            });
        }else{
            email.requestFocus();
        }
    }

    private void UpdateUserPassword(User u) {

        UserDao dao = new UserDao();
        dao.update(u, (items, message)->{
            Log.i(TAG , "UpdateUserPassword: "+message);
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