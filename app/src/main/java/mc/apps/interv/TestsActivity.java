package mc.apps.interv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class TestsActivity extends AppCompatActivity {
    private static final String TAG = "tests";

    //@RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);

        findViewById(R.id.btn_route_calcul).setOnClickListener(view -> showAlertDialogButtonClicked());
    }
    public void showAlertDialogButtonClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Observations");

        final View customLayout = getLayoutInflater().inflate(R.layout.rapport_input_layout, null);
        builder.setView(customLayout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            EditText editText = customLayout.findViewById(R.id.edtIntervRapport);
            sendDialogDataToActivity(editText.getText().toString());
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendDialogDataToActivity(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

/*    private void calcRoute() {
        //gMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.i(TAG , "SupportMapFragment : "+mapFragment);
        mapFragment.getMapAsync(this);
    }

    GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final String editDepart = "17 rue saint antoine 69003 Lyon France";    //getIntent().getStringExtra("DEPART");
        final String editArrivee = "2 rue gabillot 69003 Lyon France";         //getIntent().getStringExtra("ARRIVEE");

        new ItineraireTask(this, mMap, editDepart, editArrivee).execute();
    }

    private class ItineraireTask extends AsyncTask<Void, Integer, Boolean> {
        private static final String TOAST_MSG = "Calcul de l'itinéraire en cours";
        private static final String TOAST_ERR_MAJ = "Impossible de trouver un itinéraire";

        private Context context;
        private GoogleMap gMap;
        private String editDepart;
        private String editArrivee;
        private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();


        public ItineraireTask(final Context context, final GoogleMap gMap, final String editDepart, final String editArrivee) {
            this.context = context;
            this.gMap = gMap;
            this.editDepart = editDepart;
            this.editArrivee = editArrivee;
        }


        @Override
        protected void onPreExecute() {
            Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //Construction de l'url à appeler
                final StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/xml?sensor=false&language=fr");
                url.append("&origin=");
                url.append(editDepart.replace(' ', '+'));
                url.append("&destination=");
                url.append(editArrivee.replace(' ', '+'));

                //Appel du web service
                final InputStream stream = new URL(url.toString()).openStream();
                Log.i(TAG, "doInBackground: url.toString()="+url.toString());
                Log.i(TAG, "doInBackground: stream="+stream);

                //Traitement des données
                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringComments(true);

                final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                final Document document = documentBuilder.parse(stream);
                document.getDocumentElement().normalize();

                //On récupère d'abord le status de la requête
                final String status = document.getElementsByTagName("status").item(0).getTextContent();
                Log.i(TAG, "doInBackground: status="+status);

                if (!"OK".equals(status)) {
                    return false;
                }

                //On récupère les steps
                final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
                final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
                final int length = nodeListStep.getLength();

                for (int i = 0; i < length; i++) {
                    final Node nodeStep = nodeListStep.item(i);

                    if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elementStep = (Element) nodeStep;

                        //On décode les points du XML
                        decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                    }
                }
                Log.i(TAG, "doInBackground: OK");
                return true;
            } catch (final Exception e) {
                Log.i(TAG, "doInBackground: Error = "+e);
                return false;
            }
        }

        private void decodePolylines(final String encodedPoints) {
            int index = 0;
            int lat = 0, lng = 0;

            while (index < encodedPoints.length()) {
                int b, shift = 0, result = 0;

                do {
                    b = encodedPoints.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;

                do {
                    b = encodedPoints.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                lstLatLng.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
            }
        }

        *//**
         * {@inheritDoc}
         *//*
        @Override
        protected void onPostExecute(final Boolean result) {
            if (!result) {
                Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();
            } else {
                //On déclare le polyline, c'est-à-dire le trait (ici bleu) que l'on ajoute sur la carte pour tracer l'itinéraire
                final PolylineOptions polylines = new PolylineOptions();
                polylines.color(Color.BLUE);

                //On construit le polyline
                for (final LatLng latLng : lstLatLng) {
                    polylines.add(latLng);
                }

                //On déclare un marker vert que l'on placera sur le départ
                final MarkerOptions markerA = new MarkerOptions();
                markerA.position(lstLatLng.get(0));
                markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                //On déclare un marker rouge que l'on mettra sur l'arrivée
                final MarkerOptions markerB = new MarkerOptions();
                markerB.position(lstLatLng.get(lstLatLng.size() - 1));
                markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                //On met à jour la carte
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLatLng.get(0), 10));
                gMap.addMarker(markerA);
                gMap.addPolyline(polylines);
                gMap.addMarker(markerB);
            }
        }
    }*/
}