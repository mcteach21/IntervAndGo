package mc.apps.interv;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import mc.apps.interv.dao.GpsDao;
import mc.apps.interv.dao.UserDao;
import mc.apps.interv.model.GpsPosition;
import mc.apps.interv.model.User;

public class ShowInMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG ="tests";
    private GoogleMap mMap;
    private String supervisor_filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_in_map);

        Intent intent = getIntent();
        supervisor_filter = intent.getStringExtra("supervisor_filter");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    List<String> filtered_techs = new ArrayList<>();
    List<User> techs = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(supervisor_filter!=null) {
            Log.i(TAG , "onMapReady: supervisor_filter="+supervisor_filter);

            UserDao udao = new UserDao();
            udao.findBySupervisor(supervisor_filter, (items,mess)->{
                techs = udao.Deserialize(items, User.class);
                Log.i(TAG , "onMapReady: techs="+techs.size());

                if(techs.size()==0){
                    Toast.makeText(this, "Aucun Technicien à afficher sur la carte!", Toast.LENGTH_SHORT).show();
                }else{
                    for (User u: techs)
                        filtered_techs.add(u.getCode());
                    displayMarkers(true);
                }
            });
        }else{
            displayMarkers(false);
        }
    }

    private void displayMarkers(boolean filter) {

        Log.i(TAG , "displayMarkers: filter="+filter);

        GpsDao dao = new GpsDao();
        dao.list((items, message)->{
            List<GpsPosition> positions = dao.Deserialize(items, GpsPosition.class);

            LatLng newLatLng;
            for (GpsPosition gp: positions) {

                if(!filter || filtered_techs.contains(gp.getTechnicien_id())) {

                    newLatLng = new LatLng(gp.getLatitude(), gp.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(newLatLng).title(gp.getTechnicien_id()));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));

                    LatLng adress = newLatLng;
                    new Handler().postDelayed(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(adress, 14)), 800);
                }
            }
        });
    }

}