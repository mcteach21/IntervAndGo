package mc.apps.demo0.libs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mc.apps.demo0.R;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class PDFUtil {
    private static final String TAG = "tests";
    public static final double PDF_PAGE_WIDTH = 8.3 * 72 * 1.2;
    public static final double PDF_PAGE_HEIGHT = 11.7 * 72 * 2.5;
    private static final String PDFS_DIRECTORY_NAME = "pdfs";

    // public static final double PDF_PAGE_WIDTH_INCH = 8.3;
    // public static final double PDF_PAGE_HEIGHT_INCH = 11.7;

    private static PDFUtil sInstance;
    private PDFUtil() {

    }
    public static PDFUtil getInstance() {
        if (sInstance == null) {
            sInstance = new PDFUtil();
        }
        return sInstance;
    }

    private Context context;
    public final void generatePDF(Context context, final List<View> contentViews, final String fileName, final PDFUtilListener listener) {
        this.context = context;

        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            Log.i(TAG, "Generate PDF available for your android version..");
            new GeneratePDFAsync(contentViews, fileName, listener).execute();
        } else {
            // Before Kitkat
            Log.i(TAG, "Generate PDF is not available for your android version.");
            listener.pdfGenerationFailure(new APINotSupportedException("Generate PDF is not available for your android version."));
        }

    }

    public interface PDFUtilListener {
        void pdfGenerationSuccess(File savedPDFFile);
        void pdfGenerationFailure(final Exception exception);
    }
    private class GeneratePDFAsync extends AsyncTask<Void, Void, File> {
        private List<View> contentViews;
        private String fileName;
        private PDFUtilListener listener = null;

        public GeneratePDFAsync(final List<View> contentViews, final String fileName, final PDFUtilListener listener) {
            Log.i(TAG, "GeneratePDFAsync..");

            this.contentViews = contentViews;
            this.fileName = fileName;
            this.listener = listener;
        }
        @Override
        protected File doInBackground(Void... params) {
            try {
                PdfDocument pdfDocument = new PdfDocument();
                writePDFDocument(pdfDocument);

                Log.i(TAG, "GeneratePDFAsync..doInBackground.. Save document to file..");
                return savePDFDocumentToStorage(pdfDocument);
            } catch (Exception exception) {
                Log.e(TAG, exception.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(File savedPDFFile) {
            super.onPostExecute(savedPDFFile);
            Log.i(TAG, "GeneratePDFAsync..onPostExecute..");
            if (savedPDFFile != null) {
                Log.i(TAG, "GeneratePDFAsync..onPostExecute..Send Success callback.");
                listener.pdfGenerationSuccess(savedPDFFile);
            } else {
                Log.i(TAG, "GeneratePDFAsync..onPostExecute..Send Error callback.");
                //listener.pdfGenerationFailure(mException);
            }
        }


        private void writePDFDocument(final PdfDocument pdfDocument) {
            Log.i(TAG, "GeneratePDFAsync..writePDFDocument..");

            for (int i = 0; i < contentViews.size(); i++) {
                View contentView = contentViews.get(i);

               /* int VIEW_WIDTH = contentView.getWidth();
                int VIEW_HEIGHT = contentView.getHeight();

                Log.i(TAG, "writePDFDocument: "+VIEW_WIDTH+" "+VIEW_HEIGHT);*/

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int) PDF_PAGE_WIDTH, (int) PDF_PAGE_HEIGHT, i + 1).create();
                // PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(VIEW_WIDTH, VIEW_HEIGHT, i + 1).create();

                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas pageCanvas = page.getCanvas();
                pageCanvas.scale(1f, 1f);

                int pageWidth = pageCanvas.getWidth();
                int pageHeight = pageCanvas.getHeight();

                int measureWidth = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY);
                int measuredHeight = View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY);

                contentView.measure(measureWidth, measuredHeight);
                contentView.layout(0, 0, pageWidth, pageHeight);
                contentView.draw(pageCanvas);

                Log.i(TAG, "GeneratePDFAsync..writePDFDocument..finish the page");
                pdfDocument.finishPage(page);

            }
        }
        private File savePDFDocumentToStorage(final PdfDocument pdfDocument) throws IOException {

            Log.i(TAG, "GeneratePDFAsync..savePDFDocumentToStorage..");
            FileOutputStream fos = null;

            // Create file.
            File files_directory = context.getExternalFilesDir(PDFS_DIRECTORY_NAME);
            File pdfFile = new File(files_directory, "/" + fileName + ".pdf");

            /*//Create parent directories
            File parentFile = pdfFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                throw new IllegalStateException("Couldn't create directory: " + parentFile);
            }*/
            boolean fileExists = pdfFile.exists();
            if (fileExists) {
                fileExists = !pdfFile.delete();
            }
            try {
                if (!fileExists) {
                    // Create New File.
                    fileExists = pdfFile.createNewFile();
                }

                if (fileExists) {
                    fos = new FileOutputStream(pdfFile);
                    pdfDocument.writeTo(fos);
                    fos.close();
                    pdfDocument.close();
                }
                Log.i(TAG, "GeneratePDFAsync..savePDFDocumentToStorage..return "+pdfFile);
                return pdfFile;
            } catch (IOException exception) {
                exception.printStackTrace();
                if (fos != null) {
                    fos.close();
                }
                throw exception;
            }
        }
    }

    /**
     * APINotSupportedException will be thrown If the device doesn't support PDF methods.
     */
    private static class APINotSupportedException extends Exception {
        // mErrorMessage.
        private String mErrorMessage;

        /**
         * Constructor.
         *
         * @param errorMessage Error Message.
         */
        public APINotSupportedException(final String errorMessage) {
            this.mErrorMessage = errorMessage;
        }

        /**
         * To String.
         *
         * @return error message as a string.
         */
        @Override
        public String toString() {
            return "APINotSupportedException{" +
                    "mErrorMessage='" + mErrorMessage + '\'' +
                    '}';
        }
    }

    /**
     * Convert PDF to bitmap, only works on devices above LOLLIPOP
     *
     * @param pdfFile pdf file
     * @return list of bitmap of every page
     * @throws Exception
     */
    public static ArrayList<Bitmap> pdfToBitmap(File pdfFile) throws Exception, IllegalStateException {
        if (pdfFile == null || pdfFile.exists() == false) {
            throw new IllegalStateException("");
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            throw new Exception("PDF preview image cannot be generated in this device");
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return null;
        }

        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);


                int width = page.getWidth();
                int height = page.getHeight();

                /* FOR HIGHER QUALITY IMAGES, USE:
                int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                */

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                bitmaps.add(bitmap);

                // close the page
                page.close();

            }

            // close the renderer
            renderer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmaps;

    }

    public static void OpenPDF(Context context, File file) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "PDF Reader not found!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * v2
     */
    public static void createPdfFromContent(Activity context, String[] contents, List<Bitmap> signatures, List<Bitmap> photos, String fileName) {
        File files_directory = context.getExternalFilesDir(PDFS_DIRECTORY_NAME);
        File pdfFile = new File(files_directory, "/" + fileName + ".pdf");

       /* WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;
        int convertHighet = (int) hight, convertWidth = (int) width;*/

        try {
            FileOutputStream fOut = new FileOutputStream(pdfFile);

            PdfDocument document = new PdfDocument();
           /* PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();*/

  /*          int pageWidth = canvas.getWidth();
            int pageHeight = canvas.getHeight();*/

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int) PDF_PAGE_WIDTH, (int) PDF_PAGE_HEIGHT,  1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.scale(1f, 1f);

            //canvas.setDensity(72);

           /* int measureWidth = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY);
            int measuredHeight = View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY);*/

            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_intervention);
            drawable.setBounds(0, 0, 150, 150);
            drawable.draw(canvas);

            Paint titlePaint = new Paint();
            titlePaint.setColor(Color.BLUE);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(50);
            canvas.drawText("Rapport Intervention", 160, 80, titlePaint);

            Paint paint = new Paint();
            paint.setTextSize(30);
            int n=0, left, top;
            //float textHeight;
            for (String content: contents) {
                paint.setColor(Color.BLACK);
                String[] parts = content.split("\n");

                int MAX_WIDTH = (int) PDF_PAGE_WIDTH-100;
                left=50;
                for (String part: parts) {
                    if(!part.trim().equals("")) {
                        top=200 + 50 * n++;
                        if(part.length() > 50){
                            paint.setSubpixelText(true);
                            n += drawTextAndBreakLine(canvas, paint, left, top, MAX_WIDTH , part) - 1;
                        }else {
                            canvas.drawText(part, left, top, paint);
                        }
                    }
                }

                paint.setColor(Color.BLUE);
                canvas.drawLine(50,200+50*n,(int) PDF_PAGE_WIDTH-50,200+50*n++, paint);
            }


            /*paint.setColor(Color.RED);
            paint.setStrokeWidth(20);
            canvas.drawRect(50, 200+50*n, 250, 300+50*n++, paint);
*/
            int start;
            int i=0;
            Rect rectangle;

            start = 200 + 50 * n;
            for (Bitmap bitmap: signatures) {

                if(i%2==0)
                    rectangle = new Rect(50, start, 300, start + 250);
                else
                    rectangle = new Rect(350, start, 600, start + 250);
                canvas.drawBitmap(bitmap, null, rectangle, null);

                if(i%2!=0)
                    start = start + 250;

                Log.i(TAG, "createPdfFromContent: i => start = "+i+" => " + start);
                i++;
            }

            paint.setColor(Color.RED);
            canvas.drawLine(50,start,(int) PDF_PAGE_WIDTH-50,start, paint);

            i=0;
            start = start + 50;
            for (Bitmap bitmap: photos) {
                if(i%2==0)
                    rectangle = new Rect(50, start, 300, start + 250);
                else
                    rectangle = new Rect(350, start, 600, start + 250);
                canvas.drawBitmap(bitmap, null, rectangle, null);

                if(i%2!=0)
                    start = start + 250;

                Log.i(TAG, "createPdfFromContent: i => start = "+i+" => " + start);
                i++;
            }

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

            OpenPDF(context, pdfFile);
        } catch (IOException e) {
            Log.i("error", e.getLocalizedMessage());
        }
    }
    public static int drawTextAndBreakLine(final Canvas canvas, final Paint paint, final float x, final float y, final float maxWidth, final String text) {
        String textToDisplay = text;
        String tempText = "";
        char[] chars;
        float textHeight = paint.descent() - paint.ascent();
        float lastY = y;
        int nextPos = 0;
        int lengthBeforeBreak = textToDisplay.length();

        int line=0;
        do {
            lengthBeforeBreak = textToDisplay.length();
            chars = textToDisplay.toCharArray();
            nextPos = paint.breakText(chars, 0, chars.length, maxWidth, null);
            tempText = textToDisplay.substring(0, nextPos);
            textToDisplay = textToDisplay.substring(nextPos, textToDisplay.length());
            canvas.drawText(tempText, x, lastY, paint);
            lastY += textHeight;

            line++;
        } while(nextPos < lengthBeforeBreak);

       return line;
    }
}

