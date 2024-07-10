package com.example.testmap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.testmap.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                            searchNearbyCarRentals(userLocation);
                        }
                    }
                });
    }

    private void searchNearbyCarRentals(LatLng location) {
        String apiKey = getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude +
                "&radius=5000&type=car_rental&key=" + apiKey;

        new GetNearbyPlaces().execute(url);
    }

    private class GetNearbyPlaces extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String data = "";
            try {
                data = downloadUrl(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject place = results.getJSONObject(i);
                    String placeId = place.getString("place_id");
                    fetchPlaceDetails(placeId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchPlaceDetails(String placeId) {
        String apiKey = getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + apiKey;

        new FetchPlaceDetails().execute(url);
    }

    private class FetchPlaceDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String data = "";
            try {
                data = downloadUrl(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject resultObject = jsonObject.getJSONObject("result");
                String name = resultObject.getString("name");
                String address = resultObject.getString("formatted_address");
                JSONObject location = resultObject.getJSONObject("geometry").getJSONObject("location");
                LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));

                Location userLocation = new Location("");
                userLocation.setLatitude(latLng.latitude);
                userLocation.setLongitude(latLng.longitude);

                Location placeLocation = new Location("");
                placeLocation.setLatitude(location.getDouble("lat"));
                placeLocation.setLongitude(location.getDouble("lng"));

                float distance = userLocation.distanceTo(placeLocation) / 1000; // Convert to kilometers

                mMap.addMarker(new MarkerOptions().position(latLng).title(name)
                        .snippet("Address: " + address + "\nDistance: " + String.format("%.2f", distance) + " km"));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View infoView = getLayoutInflater().inflate(R.layout.custom_info, null);
                        TextView title = infoView.findViewById(R.id.title);
                        TextView snippet = infoView.findViewById(R.id.snippet);

                        title.setText(marker.getTitle());
                        snippet.setText(marker.getSnippet());

                        return infoView;
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
