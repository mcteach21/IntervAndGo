package mc.apps.demo0.ui.additem;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import mc.apps.demo0.AdminActivity;
import mc.apps.demo0.ClientsActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.service.InterventionManager;
import mc.apps.demo0.service.UserManager;
import mc.apps.demo0.ui.technician.TechnicianFragments;
import mc.apps.demo0.viewmodels.MainViewModel;

public class AddItemFragment extends Fragment {
    private static final String TAG = "tests";
    //private MainViewModel mainViewModel;

    private View root ;
    private int[] fragments_layouts = {
            R.layout.fragment_superv_planif,
            R.layout.fragment_user_add
    };
    private String[] fragments_titles = {
            "PLanifier Intervention",
            "Ajout Utilisateur"
    };

    private static int num=0;
    public static AddItemFragment newInstance(int num) {
        AddItemFragment.num = num;
        return new AddItemFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(fragments_layouts[num-1], container, false);
        TextView title = root.findViewById(R.id.fragment_title);
        title.setText(fragments_titles[num-1]);


        //mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(num==1) {
            prepareAddingIntervention();
        }else if(num==2) {
            prepareAddingUser();
        }
    }

    private void prepareAddingUser() {
        UserManager manager = new UserManager(getActivity());
        manager.prepareAddUser(root, AdminActivity.class);
    }

    private void prepareAddingIntervention() {
        //gérer ajout intervention (planification)!
        InterventionManager manager = new InterventionManager(getActivity());
        manager.prepareAddIntervention(root, AdminActivity.class);
    }

}