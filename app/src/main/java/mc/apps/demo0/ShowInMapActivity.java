package mc.apps.demo0;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mc.apps.demo0.dao.GpsDao;
import mc.apps.demo0.model.GpsPosition;

public class ShowInMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_in_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        GpsDao dao = new GpsDao();
        dao.list((items,message)->{
            List<GpsPosition> positions = dao.Deserialize(items, GpsPosition.class);

            LatLng newLatLng;
            //List<LatLng> markerPoints = new ArrayList<LatLng>();
            for (GpsPosition gp: positions) {
                newLatLng = new LatLng(gp.getLatitude(), gp.getLongitude());
                //markerPoints.add(newLatLng);

                mMap.addMarker(new MarkerOptions().position(newLatLng).title(gp.getTechnicien_id()));
                mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );

                LatLng adress = newLatLng;
                new Handler().postDelayed(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(adress, 14)), 800);
            }

            //displayMarkers(markerPoints);
        });
    }

  /*  private void displayMarkers(List<LatLng> markerPoints) {

        Iterator<LatLng> iterator = markerPoints.iterator();
        while (iterator.hasNext()) {
            MarkerOptions markerOptions = new MarkerOptions().position(iterator.next());

           *//* mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*//*
            mMap.addMarker(markerOptions);
        }
    }*/
}