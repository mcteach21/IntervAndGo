package mc.apps.demo0.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.R;
import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "tests" ;

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);

        return fragment;
    }

    int index = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        if (getArguments() != null)
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int layout = (index==1)?R.layout.fragment_superv_intervs:R.layout.fragment_superv_planif;
        View root = inflater.inflate(layout, container, false);
        final TextView textView = root.findViewById(R.id.fragment_title);

        pageViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        return root;
    }

    AutoCompleteTextView codeClient;
    EditText desc, dateDebut, dateFin, serviceCible, comment;
    RecyclerView technicians;

    MainViewModel mainViewModel;
    List<User> selected = new ArrayList();

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        if (index==2){

            initAutocomplete(root);
            initListTech(root);


            Button btnadd = root.findViewById(R.id.btn_add_rapport);
            btnadd.setOnClickListener(view -> {

                //codeClient = root.findViewById(R.id.edtCodeClient);
                Toast.makeText(root.getContext(), codeClient.getText().toString(), Toast.LENGTH_SHORT).show();

                desc = root.findViewById(R.id.edtDesc);
                dateDebut = root.findViewById(R.id.edtDateDebutPrev);
                dateFin = root.findViewById(R.id.edtDateFinPrev);

                serviceCible = root.findViewById(R.id.edtServiceCible);
                comment = root.findViewById(R.id.edtComment);

                Intervention interv = new Intervention(0, 1 , //codeClient,
                        desc.getText().toString(),
                        dateDebut.getText().toString(),
                        dateFin.getText().toString(),
                        serviceCible.getText().toString(),
                        comment.getText().toString());

                InterventionDao dao = new InterventionDao();
                dao.add(interv, (items, message) -> {
                    Log.i(TAG, "onCreate: "+message);
                    Toast.makeText(root.getContext(), "Intervention planifiée!", Toast.LENGTH_LONG).show();
                });
                resetFields(root); //reinitialiser form planfication!
            });
        }
    }

    private void initAutocomplete(View root) {
        ClientDao dao = new ClientDao();

        dao.list((data, message) -> {
            List<Client> items = dao.Deserialize(data, Client.class);
            ArrayAdapter<Client> adapter = new ArrayAdapter<Client>(
                    root.getContext(),
                    android.R.layout.select_dialog_item,
                    items);

            codeClient = root.findViewById(R.id.edtCodeClient);
            codeClient.setThreshold(1);       //will start working from first character
            codeClient.setAdapter(adapter);
            codeClient.setTextColor(Color.WHITE);
        });
    }
    private void initListTech(View root){

        technicians = root.findViewById(R.id.tech_list);
        technicians.setHasFixedSize(true);

        GridLayoutManager layoutManager2 = new GridLayoutManager(root.getContext(), 2);
        technicians.setLayoutManager(layoutManager2);

        //selected.add(new User(0, "", "", "Aucun technicien!", "", (byte) 3));
        SelectedUsersAdapter adapter = new SelectedUsersAdapter(
                selected,
                null,
                true
        );
        technicians.setAdapter(adapter);
        mainViewModel.getSelected().observe(getActivity(), selected -> {
                    Log.i("tests", "loadList: "+selected);
                    adapter.refresh(selected);
                });
    }

    private void resetFields(View root) {
        codeClient.getText().clear();
        desc.getText().clear();
        dateDebut.getText().clear();
        dateFin.getText().clear();
        serviceCible.getText().clear();
        comment.getText().clear();
    }
}