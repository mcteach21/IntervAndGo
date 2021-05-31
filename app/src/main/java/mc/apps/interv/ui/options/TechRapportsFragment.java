package mc.apps.interv.ui.options;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import mc.apps.interv.R;

public class TechRapportsFragment extends Fragment {
    private OptionsViewModel optionsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        optionsViewModel = ViewModelProviders.of(this).get(OptionsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_tech_main00, container, false);
        final TextView textView = root.findViewById(R.id.fragment_title);

        optionsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("[Technicien] "+s);
            }
        });
        return root;
    }
}
