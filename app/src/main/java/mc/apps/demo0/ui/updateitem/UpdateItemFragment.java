package mc.apps.demo0.ui.updateitem;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mc.apps.demo0.AdminActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.User;
import mc.apps.demo0.service.ClientManager;
import mc.apps.demo0.service.InterventionManager;
import mc.apps.demo0.service.UserManager;
import mc.apps.demo0.viewmodels.MainViewModel;

public class UpdateItemFragment extends Fragment {
    private static final String TAG = "tests";
    private MainViewModel mainViewModel;
    private TextView title;

    private View root ;
    private int[] fragments_layouts = {
            R.layout.fragment_superv_planif, //TODO ...
            R.layout.fragment_user_add,
            R.layout.fragment_client_add
    };
    private String[] fragments_titles = {
            "Modifier Intervention", //TODO ...
            "Modifier Utilisateur",
            "Modifier Client"
    };

    private static int num=0;
    public static UpdateItemFragment newInstance(int num) {
        UpdateItemFragment.num = num;
        return new UpdateItemFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(fragments_layouts[num-1], container, false);
        title = root.findViewById(R.id.fragment_title);
        title.setText(fragments_titles[num-1]);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        //Log.i(TAG, "onCreateView: mainViewModel.getItem() = "+mainViewModel.getItem().getValue());

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(num==1) {
            //prepareUpdatingIntervention(); //TODO ...
        }else if(num==2) {
            prepareUpdatingUser();
        }else if(num==3) {
            prepareUpdatingClient();
        }
    }

    private void prepareUpdatingClient() {
        Client client = (Client) mainViewModel.getItem().getValue();
        title.setText(title.getText()+" : "+client.getCode());

        ((Button)root.findViewById(R.id.btn_add)).setText(R.string.btn_update_client);
        ClientManager manager = new ClientManager(getActivity());
        manager.prepareUpdateClient(client, root, AdminActivity.class);
    }

    private void prepareUpdatingUser() {
        User user = (User) mainViewModel.getItem().getValue();
        title.setText(title.getText()+" : "+user.getCode());

        ((Button)root.findViewById(R.id.btn_add)).setText(R.string.btn_update_user);
        UserManager manager = new UserManager(getActivity());
        manager.prepareUpdateUser(user, root, AdminActivity.class);
    }

    private void prepareUpdatingIntervention() {
        //gérer ajout intervention (planification)!
       /* InterventionManager manager = new InterventionManager(getActivity());
        manager.prepareAddIntervention(root, AdminActivity.class);*/
    }

}