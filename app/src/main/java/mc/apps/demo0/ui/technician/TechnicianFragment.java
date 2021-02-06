package mc.apps.demo0.ui.technician;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.ListenableWorker;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import mc.apps.demo0.R;
import mc.apps.demo0.TechnicianActivity;
import mc.apps.demo0.dao.Dao;
import mc.apps.demo0.dao.GpsDao;
import mc.apps.demo0.libs.GPSTracker;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.GpsPosition;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class TechnicianFragment extends Fragment {
    private static final String TAG = "tests";
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

        //getCurrentLocation();
    }
   /* GPSTracker gps;
    private void getCurrentLocation() {
        gps = new GPSTracker(getActivity());
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            ((TextView)root.findViewById(R.id.textLocation)).setText(latitude+"x"+longitude);
            saveGps(latitude, longitude);
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }
    }
    GpsPosition gp;
    private void saveGps(double latitude, double longitude) {
        GpsDao dao = new GpsDao();
        User user = MyTools.GetUserInSession();

        dao.find(user.getCode(), (items, message) -> {
            List<GpsPosition> positions_ = dao.Deserialize(items, GpsPosition.class);
            if(positions_.size()>0){
                gp = positions_.get(0);
                gp.setLatitude(latitude);
                gp.setLongitude(longitude);
                dao.update(gp, (items_, message_) -> {
                    Toast.makeText(getActivity(), "gps updated", Toast.LENGTH_SHORT).show();
                });
            }else{
                gp =  new GpsPosition(0,latitude, longitude, user.getCode());
                dao.add(gp, (items_, message_) -> {
                    Toast.makeText(getActivity(), "gps added", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }*/

}