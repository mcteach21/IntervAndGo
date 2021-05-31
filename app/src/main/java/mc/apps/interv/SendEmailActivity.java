package mc.apps.interv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import mc.apps.interv.libs.GMailSender;
import mc.apps.interv.libs.MyTools;

public class SendEmailActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1 ;
    private static final String SENDER_EMAIL_ID = "adm.mc69@gmail.com";
    private static final String SENDER_EMAIL_PASSWORD = "Azerty123_";
    private static final String TAG = "tests";

    EditText Recipient_Email,subject,body;
    Button send;
    ImageView imageView;
    String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        imageView=findViewById(R.id.imageView);
        send=findViewById(R.id.button);
        Recipient_Email=findViewById(R.id.editText);
        subject=findViewById(R.id.editText2);
        body=findViewById(R.id.editText3);

        imageView.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });

        send.setOnClickListener(v -> {
            String mail_subject = subject.getText().toString();
            String mail_body =  body.getText().toString()+"\n"+ MyTools.GetRandomPassword();
            String mail_sendto = Recipient_Email.getText().toString();

            new Thread(() -> {
                try {
                    GMailSender sender = new GMailSender(SENDER_EMAIL_ID , SENDER_EMAIL_PASSWORD);
                    // sender.addAttachment(picturePath);
                    sender.sendMail(mail_subject,mail_body, SENDER_EMAIL_ID, mail_sendto);
                    Log.i(TAG, "Mail Envoyé!");
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }).start();

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.i(TAG, "onActivityResult: picturePath = "+picturePath);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }


}