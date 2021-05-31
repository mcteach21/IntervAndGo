package mc.apps.interv.ui.additem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mc.apps.interv.AdminActivity;
import mc.apps.interv.R;
import mc.apps.interv.service.ClientManager;
import mc.apps.interv.service.InterventionManager;
import mc.apps.interv.service.UserManager;

public class AddItemFragment extends Fragment {
    private static final String TAG = "tests";
    //private MainViewModel mainViewModel;

    private View root ;
    private int[] fragments_layouts = {
            R.layout.fragment_superv_planif,
            R.layout.fragment_user_add,
            R.layout.fragment_client_add
    };
    private String[] fragments_titles = {
            "PLanifier Intervention",
            "Ajout Utilisateur",
            "Ajout Client"
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
        }else if(num==3) {
            prepareAddingClient();
        }
    }

    private void prepareAddingClient() {
        ClientManager manager = new ClientManager(getActivity());
        manager.prepareAddClient(root, AdminActivity.class);
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