package mc.apps.demo0.ui.technician;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.InterventionActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.adapters.ImagesAdapter;
import mc.apps.demo0.adapters.InterventionsAdapter;
import mc.apps.demo0.dao.Dao;
import mc.apps.demo0.dao.FileDao;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.libs.UploadFileAsync;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.InterventionFile;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechnicianFragments extends Fragment {
    private static final String TAG = "tests";
    private static final int TECH_INTERV_CODE = 1000;
    private MainViewModel mainViewModel;
    private View root ;
    private int[] fragments_layouts = {
            R.layout.technician_intervs_fragment,
            R.layout.technician_rapport_fragment,
            R.layout.technician_intervs_fragment   //R.layout.technician_histo_fragment
    };
    private String[] fragments_titles = {
           "Mes Interventions",
           "Saisir Rapport Intervention", //
           "Historique Interventions"
    };

    private static Intervention intervention=null;
    private static int num=0;
    public static TechnicianFragments newInstance(int num) {
        TechnicianFragments.num = num;
        TechnicianFragments.intervention = null;
        return new TechnicianFragments();
    }
    public static TechnicianFragments newInstance(int num, Intervention intervention) {
        TechnicianFragments.num = num;
        TechnicianFragments.intervention = intervention;
        return new TechnicianFragments();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(fragments_layouts[num], container, false);
        TextView title = root.findViewById(R.id.fragment_title);
        title.setText(fragments_titles[num]);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        mainViewModel.getRefresh().observe(
                getViewLifecycleOwner(),
                search -> {
                    refreshListAsync();
                }
        );
        if(num==0 || num==2) {
            //Recherche / Liste Interventions..
            mainViewModel.getSearch().observe(
                    getViewLifecycleOwner(),
                    search -> {
                        if (search == null || search.length() == 0)
                            refreshListAsync();
                        else if (adapter != null)
                            adapter.getFilter().filter(search);
                    }
            );
            refreshListAsync();
        }else if(num==1){
            //Ajouter Rapport
            initCurrentIntervention(root); //AutoCompletion sur Champ CodeClient!

            initListPhotos(root);   //liste photos / Rapport
            initSignaturesImages(root); // liste signatures / Rapport


            isOpen=false;
            ConstraintLayout interventionDetails = root.findViewById(R.id.interventionDetails);
            interventionDetails.getLayoutParams().height = 0;

            AppCompatImageButton btnShow = root.findViewById(R.id.btn_interv_details);
            btnShow.setOnClickListener(
                    v-> onSlideDetails(interventionDetails)
            );

            Button btnadd = root.findViewById(R.id.btn_add);
            btnadd.setOnClickListener(view -> {
                addRapport(root);
            });
        }
    }
    private boolean isOpen;
    private void onSlideDetails(View view){
        int currentHeight = isOpen?1200:0;
        int newHeight = isOpen?0:1200;

        //Log.i(TAG, "Pixel to DP: "+MyTools.pxToDp(1200));

        ValueAnimator slideAnimator = new ValueAnimator()
                .ofInt(currentHeight, newHeight)
                .setDuration(500);

        slideAnimator.addUpdateListener( v-> {
            int value = (int) v.getAnimatedValue();
            view.getLayoutParams().height = value;
            view.requestLayout();
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        isOpen = !isOpen;
    }


    /**
     * Saisie Rapport / Technicien
     */
    EditText codeIntervention, observations;
    TextView txtClient, txtDescription, txtConsignes, txtDatePrevue,  dateDebutR, heureDebutR, dateFinR, heureFinR;
    Spinner statutChoice;
    int statut;
    AppCompatImageView btnNow1,btnNow2;

    private void initCurrentIntervention(View root) {
        codeIntervention = root.findViewById(R.id.edtCodeInterv);
        txtClient = root.findViewById(R.id.txtClient);
        txtDescription = root.findViewById(R.id.txtDescription);
        txtConsignes = root.findViewById(R.id.txtConsignes);
        txtDatePrevue = root.findViewById(R.id.txtDatePrevue);

        dateDebutR = root.findViewById(R.id.edtDateDebutReel);
        dateFinR = root.findViewById(R.id.edtDateFinReel);

        heureDebutR = root.findViewById(R.id.edtHeureDebutReel);
        heureFinR = root.findViewById(R.id.edtHeureFinReel);

        btnNow1 = root.findViewById(R.id.btn_img_now1);
        btnNow2 = root.findViewById(R.id.btn_img_now2);

        View.OnClickListener listener = view -> {
            if(view==btnNow1){
                dateDebutR.setText(MyTools.getCurrentDateShort());
                heureDebutR.setText(MyTools.getCurrentTime());
            }else{
                dateFinR.setText(MyTools.getCurrentDateShort());
                heureFinR.setText(MyTools.getCurrentTime());
            }
        };
        btnNow1.setOnClickListener(listener);
        btnNow2.setOnClickListener(listener);

        statutChoice = root.findViewById(R.id.statutChoice);
        observations = root.findViewById(R.id.edtObservations);

        if(TechnicianFragments.intervention!=null){
            codeIntervention.setText(TechnicianFragments.intervention.getCode());
            txtClient.setText("Client : "+intervention.getClientId()); //TODO nom..
            txtDescription.setText("Description Intervention : \n"+intervention.getDescription());
            txtConsignes.setText("Consignes : \n"+intervention.getConsignes());
            String prevue = "Date Intervention Prévue \n"+MyTools.formatDateFr(intervention.getDateDebutPrevue())+" - "+MyTools.formatDateFr(intervention.getDateFinPrevue());
            txtDatePrevue.setText(prevue);

           /* UserDao udao = new UserDao();
            udao.findByCode(intervention.getSuperviseurId(), (items_, mess_)->{
                List<User> users = udao.Deserialize(items_, User.class);
                if(!users.isEmpty())
                    supervisor.setText("Superviseur : \n"+users.get(0).getFirstname()+" "+users.get(0).getLastname());
            });*/
        }
        mainViewModel.getIntervention().observe(
                getViewLifecycleOwner(),
                intervention -> {
                    if (intervention != null){
                        TechnicianFragments.intervention = intervention;
                        codeIntervention.setText(intervention.getCode());

                        txtClient.setText("Client : "+intervention.getClientId());
                        txtDescription.setText("Description Intervention : \n"+intervention.getDescription());
                        txtConsignes.setText("Consignes : \n"+intervention.getConsignes());
                        String prevue = "Date Intervention Prévue \n"+MyTools.formatDateFr(intervention.getDateDebutPrevue())+" - "+MyTools.formatDateFr(intervention.getDateFinPrevue());
                        txtDatePrevue.setText(prevue);

                        statutChoice.setSelection(intervention.getStatutId()-1);
                    }
                }
        );
    }

    private void addRapport(View root) {
        if(TechnicianFragments.intervention==null)
            return;

        TechnicianFragments.intervention.setObservations(observations.getText().toString());

        String debut = dateDebutR.getText().toString()+" "+heureDebutR.getText().toString();
        String fin = dateFinR.getText().toString()+" "+heureFinR.getText().toString();

        TechnicianFragments.intervention.setDateDebutReelle(debut.trim());
        TechnicianFragments.intervention.setDateFinReelle(fin.trim());

        statut = (int) (statutChoice.getSelectedItemId()+1);
        TechnicianFragments.intervention.setStatutId(statut);

        /**
         * TODO
         * Photos + Signatures
         */

        String INTERV_CODE = TechnicianFragments.intervention.getCode();
        Log.i(TAG, "addRapport: INTERV_CODE="+INTERV_CODE);

        ImagesAdapter adapter2 = (ImagesAdapter)signatures_list.getAdapter();
        List<Uri> signatures = adapter2.getItems();
        //UploadFileAsync upload = new UploadFileAsync();

        Log.i(TAG, "***********************************************");

        String filename, filename_short;
        FileDao fdao = new FileDao();
        for (Uri signature: signatures) {
            filename = signature.toString();
            Log.i(TAG, "addRapport: signature = "+filename);

            filename_short = filename.replace(MyTools.SIGNATURES_DIRECTORY(getContext()).getAbsolutePath()+"/","");
            Log.i(TAG, "addRapport: signature = "+filename_short);

            fdao.add(new InterventionFile(0, INTERV_CODE, filename_short, 0), (i,m)->{
                Log.i(TAG, "addRapport: add signature!");
            });
            new UploadFileAsync().execute(signature.toString());
        }
        adapter2 = (ImagesAdapter)photos_list.getAdapter();
        List<Uri> photos = adapter2.getItems();
        for (Uri photo: photos) {

            filename = getRealPathFromURI(photo);
            Log.i(TAG, "addRapport: photo = "+filename);

            String[] parts = filename.split("/");
            filename_short = parts[parts.length-1];

            fdao.add(new InterventionFile(0, INTERV_CODE, filename_short, 1), (i,m)->{
                Log.i(TAG, "addRapport: add photo!");
            });

            new UploadFileAsync().execute(filename);
        }
        Log.i(TAG, "***********************************************");

        InterventionDao dao = new InterventionDao();
        dao.update(TechnicianFragments.intervention, (items, message)->{
            Toast.makeText(root.getContext(), "Rapport ajouté avec succès!", Toast.LENGTH_SHORT).show();
            mainViewModel.setNum(0);
        });

        resetFields(); //reinitialiser form
    }

    RecyclerView photos_list;
    List<Uri> images = new ArrayList();
    private void initListPhotos(View root){
        photos_list = root.findViewById(R.id.photos_list);
        photos_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 4);
        photos_list.setLayoutManager(layoutManager2);
        ImagesAdapter adapter = new ImagesAdapter(
                images,
                null
        );
        photos_list.setAdapter(adapter);
        mainViewModel.getImages().observe(getActivity(), images -> {
            adapter.refresh(images);
        });
    }
    List<Uri> images_signatures = new ArrayList();
    RecyclerView signatures_list;
    private void initSignaturesImages(View root){
        signatures_list = root.findViewById(R.id.signatures_list);
        signatures_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 3);
        signatures_list.setLayoutManager(layoutManager2);
        ImagesAdapter adapter = new ImagesAdapter(
                images_signatures,
                null
        );
        signatures_list.setAdapter(adapter);
        mainViewModel.getSignaturesImages().observe(getActivity(), images -> {
            adapter.refresh(images);
        });
    }

    private void resetFields() {
        codeIntervention.getText().clear();

        txtDescription.setText("");
        txtClient.setText("");
        txtDatePrevue.setText("");

        statutChoice.setSelection(0);

       /* dateDebutR.getText().clear();
        dateFinR.getText().clear();*/

        observations.getText().clear();

        try {
            ((ImagesAdapter)photos_list.getAdapter()).clearItems();
            ((ImagesAdapter)signatures_list.getAdapter()).clearItems();
        }catch (Exception e){}

    }

    /**
     * Gestion liste Interventions / Technicien
     */
    List<Intervention> items = new ArrayList<Intervention>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    InterventionsAdapter adapter;

    TextView noResult;
    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        if(recyclerView==null)
            return;

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new InterventionsAdapter(
                items,
                (position, item) -> {
                    Intent intent = new Intent(root.getContext(), InterventionActivity.class);
                    intent.putExtra("intervention", (Intervention)item);
                    intent.putExtra("rapport", true);
                    getActivity().startActivityForResult(intent, TECH_INTERV_CODE);
                },
                true
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                root.getContext(), R.anim.layout_fall_down_animation
        ));

        swipeContainer = root.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshListAsync();
            }
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright
        );
    }

/*
    String pattern = "yyyy-MM-dd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private String getDate(String dateString){
        return dateString.split(" ")[0];
    }
    private String getCurrentDate(){
        return simpleDateFormat.format(new Date());
    }
*/

    private void refreshListAsync() {
        noResult = root.findViewById(R.id.noResult);
        InterventionDao dao = new InterventionDao();

        String tech_code = MyTools.GetUserInSession().getCode();
        dao.findByTech(tech_code, (data, message) -> {
            items = dao.Deserialize(data, Intervention.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                if(num==0) {
                    items = items.stream()
                            .sorted((o1, o2) -> o1.getDateDebutPrevue().compareTo(o2.getDateDebutPrevue()))
                            //TODO : filtre sur date jour ?
                            //.filter(i -> getDate(i.getDateDebutPrevue()).equals(getCurrentDate()) && i.getStatutId() != 5)
                            .filter(i -> i.getStatutId() != 5)
                            .collect(Collectors.toList());
                }else{
                    items = items.stream()
                            .sorted((o1, o2) -> o2.getDateDebutPrevue().compareTo(o1.getDateDebutPrevue()))
                            .filter(i->i.getStatutId() == 5)
                            .collect(Collectors.toList());
                }
            }

            if(noResult!=null)
                noResult.setVisibility(items.size()>0?View.GONE:View.VISIBLE);

            loadList();
            swipeContainer.setRefreshing(false);
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}