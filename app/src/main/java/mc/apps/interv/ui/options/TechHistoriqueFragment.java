package mc.apps.interv.ui.options;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import mc.apps.interv.R;
import mc.apps.interv.viewmodels.MainViewModel;

public class TechHistoriqueFragment extends Fragment {
    private MainViewModel mainViewModel;
    private TextView title;
    private View root ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tech_histo00, container, false);

        title = root.findViewById(R.id.fragment_title);
        title.setText("Historique");

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return root;
    }
}
