package mc.apps.demo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

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
            ((TextView) findViewById(R.id.reset_how_txt)).setText("Un email pour réinitiliser votre mot de passe est envoyé à l'adresse mail de votre compte.");
            findViewById(R.id.textInputLogin).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnResetPassword).setVisibility(View.INVISIBLE);
        }else{
            email.requestFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
            finish();
        return false;
    }
}