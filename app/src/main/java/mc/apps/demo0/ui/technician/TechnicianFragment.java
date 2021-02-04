package mc.apps.demo0.ui.technician;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mc.apps.demo0.R;
import mc.apps.demo0.TechnicianActivity;
import mc.apps.demo0.libs.GPSTracker;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechnicianFragment extends Fragment {
    private MainViewModel mainViewModel;

    public static TechnicianFragment newInstance() {
        return new TechnicianFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.technician_main_fragment, container, false);
    }

    View root;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.root = view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        getCurrentLocation();
    }
    GPSTracker gps;
    private void getCurrentLocation() {
        gps = new GPSTracker(getActivity());
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //TODO : save position + notification..

            ((TextView)root.findViewById(R.id.textLocation)).setText(latitude+"x"+longitude);
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }
    }

}