package mc.apps.interv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executor;
import mc.apps.interv.dao.UserDao;
import mc.apps.interv.libs.MyTools;
import mc.apps.interv.model.User;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "tests";
    ImageView logo;
    ConstraintLayout root, login_root;
    EditText login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        //masquer barre d'actions/menus
        getSupportActionBar().hide();

        //Lancer animation logo + apparition form login
        startAnimation();

        // Gestion Boutons + liens
        handleActions();

        //fingerprint init
        initFingerPrint();
    }

    private SharedPreferences sharedpreferences;
    private static final String prefs = "preferences";
    private static final String FingerPrintKey1 = "FingerPrintEmail";
    private static final String FingerPrintKey2 = "FingerPrintPassword";

    private void initFingerPrint() {
        TextView fpLink = findViewById(R.id.txtlinkfingerprint);

        sharedpreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE);
        String fingerPrintEmail="";
        if (sharedpreferences.contains(FingerPrintKey1))
            fingerPrintEmail = sharedpreferences.getString(FingerPrintKey1, "");

        noFingerPrint = "".equals(fingerPrintEmail);
        fpLink.setText(noFingerPrint?R.string.fingerprint_config_text:R.string.fingerprint_use_text);
        fpLink.setOnClickListener(v->useBiometric());
    }

    private boolean noFingerPrint=true;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private void useBiometric() {

        login = findViewById(R.id.edtlogin);
        password = findViewById(R.id.edtpassword);

        if(noFingerPrint && (login.getText().equals("") || password.getText().equals(""))){
            Toast.makeText(this, "Authentification par empreinte : saisir email et mot de passe du compte à associer!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(noFingerPrint){
            UserDao dao = new UserDao();
            dao.login(login.getText().toString(), password.getText().toString(), (data, message) -> {
                List<User> users = dao.Deserialize(data, User.class);
                if(!users.isEmpty()){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(FingerPrintKey1, login.getText().toString());
                    editor.putString(FingerPrintKey2,  password.getText().toString());
                    editor.commit();
                }else {
                    Toast.makeText(this, "Authentification par empreinte : saisir email et mot de passe du compte à associer!", Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(StartActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                if(noFingerPrint){
                    Toast.makeText(StartActivity.this, "Authentification par empreinte : compte à associé!", Toast.LENGTH_SHORT).show();
                }else {
                    login = findViewById(R.id.edtlogin);
                    password = findViewById(R.id.edtpassword);

                    sharedpreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE);
                    String fingerPrintEmail = sharedpreferences.getString(FingerPrintKey1, "");
                    String fingerPrintPassword = sharedpreferences.getString(FingerPrintKey2, "");

                    login.setText(fingerPrintEmail);
                    password.setText(fingerPrintPassword);
                }

                handleLogin();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Empreinte non reconnue!", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentification")
                .setSubtitle("Utiliser l'empreinte digitale")
                .setNegativeButtonText("Se connecter par mot de passe")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
    @Override
    public void onBackPressed() {
        MyTools.confirmExit(this);
        return;
    }

    private void handleActions() {
        Button btnsignin = findViewById(R.id.btnsignin);
        TextView linkforgotten = findViewById(R.id.txtlinkfortgotten);

        View.OnClickListener ecouteur = view -> {
            if(view.getId()==R.id.btnsignin) {
                handleLogin();          // gestion login
            }else {
                Intent intent = new Intent(StartActivity.this, PasswordActivity.class);
                startActivity(intent);
            }
        };

        //liaison gestionnaire evenement (click) bouton + links
        btnsignin.setOnClickListener(ecouteur);
        linkforgotten.setOnClickListener(ecouteur);
    }

    private void handleLogin() {
        login = findViewById(R.id.edtlogin);
        password = findViewById(R.id.edtpassword);

        String login_txt = login.getText().toString();
        String password_txt = password.getText().toString();
        UserDao dao = new UserDao();
        dao.login(login_txt, password_txt, (data, message) -> {
            List<User> users = dao.Deserialize(data, User.class);
            if(!users.isEmpty()){
                User user = users.get(0);

                Log.i(TAG, "user : "+user+" ==> "+user.getActivated());
                if(user.getActivated()==1) {
                    //compte activé..
                    MyTools.SetUserInSession(user);
                    Toast.makeText(StartActivity.this, "Bienvenue " + user.getFirstname(), Toast.LENGTH_SHORT).show();
                    openActivity(user); //ouvrir nvlle fenêtre / profil utilisateur connecté!
                }else{
                    //..compte avec mot de passe temporaire
                    Intent intent = new Intent(StartActivity.this, PasswordResetActivity.class);
                    intent.putExtra("user", (Serializable) user);
                    startActivity(intent);
                }

            }else {
                Toast.makeText(StartActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openActivity(User user) {
        Class<?> class_activity;
        switch (user.getProfilId()){
            case 1:
                class_activity = AdminActivity.class;
                break;
            case 2:
                class_activity = SupervisorActivity.class;
                break;
            default:
                class_activity = TechnicianActivity.class;
        }

        Intent intent = new Intent(StartActivity.this, class_activity);
        intent.putExtra("user", (Serializable) user);
        startActivity(intent);
                
        finish();
    }

    private void startAnimation() {

        logo = findViewById(R.id.logo);
        root = findViewById(R.id.root);
        login_root = findViewById(R.id.login_root);
        login_root.setAlpha(0f);

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        fadeAnim.setDuration(2500);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                //((ObjectAnimator)animation).getTarget();
                animateLogo();
            }
        });

        animatorSet.play(fadeAnim);
        animatorSet.start();
    }

    private void animateLogo() {
        ConstraintSet finishingConstraintSet= new ConstraintSet();
        finishingConstraintSet.clone(getApplicationContext(), R.layout.activity_start_final);

        TransitionManager.beginDelayedTransition(root);
        finishingConstraintSet.applyTo(root);

        logo.setImageDrawable(getResources().getDrawable(R.drawable.ic_app_logo_red,null));

        ValueAnimator fade1Anim = ObjectAnimator.ofFloat(login_root, "alpha", 0f, 1f).setDuration(1500);
        //ValueAnimator fade2Anim =ObjectAnimator.ofFloat(btn2, "alpha", 0f, 1f).setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fade1Anim); //.before(fade2Anim);
        animatorSet.start();
    }
}