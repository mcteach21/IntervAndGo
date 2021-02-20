package mc.apps.demo0;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.libs.PaintView;

public class SignatureActivity extends AppCompatActivity {
    private final static String TAG = "Main";
    private static final String SIGNATURES_DIRECTORY_NAME = "signatures";
    private PaintView mPaintView;
    private LinearLayout mLlCanvas;

    TextView RedDot,GreenDot,BlueDot;
    Button SaveBtn,ClearBtn ;
    String Front_Image="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        mLlCanvas = (LinearLayout)findViewById(R.id.canvas);
        mPaintView = new PaintView(this, null);
        mLlCanvas.addView(mPaintView, 0);
        mPaintView.requestFocus();

        SaveBtn= findViewById(R.id.btnSave);
        SaveBtn.setOnClickListener(v -> {
            String file = savingFile();

            Intent intent = new Intent();
            intent.putExtra("file", file);
            setResult(RESULT_OK, intent);

            finish();
        });

        ClearBtn= findViewById(R.id.btnReset);
        ClearBtn.setOnClickListener(v -> mPaintView.clear());
    }

    private String savingFile() {

        String tech_code = MyTools.GetUserInSession().getCode();
        String currentDate = new SimpleDateFormat("ddMMHHmmss", Locale.getDefault()).format(new Date());
        String fileName = tech_code+"_sig"+ currentDate;

        File files_directory = MyTools.SIGNATURES_DIRECTORY(SignatureActivity.this);
        File file = new File(files_directory, "/" + fileName + ".png");

        boolean success = false;
        if (!file.exists()) {
            try {
                success = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream ostream = null;
        try {
            ostream = new FileOutputStream(file);

            Bitmap well = mPaintView.getBitmap();
            Bitmap save = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0, 0, 320, 480), paint);
            now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), new Rect(0, 0, 320, 480), null);

            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            BitMapToString(save);

            Toast.makeText(this, "Signature sauvegardée!", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur sauvegarde..", Toast.LENGTH_SHORT).show();
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File error", Toast.LENGTH_SHORT).show();
        }

        return file.getAbsolutePath();
    }

    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        Front_Image = Base64.encodeToString(b, Base64.DEFAULT);
        return Front_Image;
    }
}