package com.example.my_health;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class findlabs extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1001;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private ListView hospitalListView;
    private BroadcastReceiver wifiReceiver;

    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findlabs);
        wifiReceiver = new WifiReceiver();

        // Check for Wi-Fi connection before proceeding
        if (isWifiConnected()) {
            // Continue with the activity initialization
            // ...

            // Register the WifiReceiver dynamically
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(wifiReceiver, filter);
        } else {
            // Display a message or take appropriate action
            Toast.makeText(this, "No Wi-Fi connection available", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if no Wi-Fi connection
        }



        unregisterReceiver(wifiReceiver);

        hospitalListView = findViewById(R.id.hospitalListView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkLocationPermission()) {
            fetchNearbyHospitals();
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchNearbyHospitals();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchNearbyHospitals() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            List<String> hospitals = getNearbyHospitals(location);
                            displayHospitals(hospitals);
                        } else {
                            Toast.makeText(findlabs.this, "Location not available.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class FetchHospitalsTask extends AsyncTask<Location, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Location... locations) {
            Location location = locations[0];
            return getNearbyHospitalsFromAPI(location);
        }

        @Override
        protected void onPostExecute(List<String> hospitals) {

            displayHospitals(hospitals);
        }
    }

    private void displayHospitals(List<String> hospitals) {
        if (hospitals != null) {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(findlabs.this, android.R.layout.simple_list_item_1, hospitals);
            hospitalListView.setAdapter(adapter);
        } else {
            Toast.makeText(findlabs.this, "Failed to fetch hospitals.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getNearbyHospitalsFromAPI(Location location) {
        List<String> hospitals = new ArrayList<>();
        try {
            // Replace "YOUR_API_KEY" with your actual API key
            String apiKey = "AIzaSyCxA-TPOfKf2amGVo5J6nlYRLJrGJNxxxY";
            String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    location.getLatitude() + "," + location.getLongitude() +
                    "&radius=5000&type=hospital&key=" + apiKey;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject placeObject = resultsArray.getJSONObject(i);
                    String name = placeObject.getString("name");
                    hospitals.add(name);
                }
            } else {
                Log.e("API Error", "Response Code: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("API Error", e.getMessage());
        }
        return hospitals;
    }


















    private List<String> getNearbyHospitals(Location location) {
        // Dummy data for demonstration purposes
        List<String> hospitals = new ArrayList<>();
        hospitals.add("Iqra Medical Lab, Faisal Town, Lhr");
        hospitals.add("Services Lab");
        hospitals.add("Chughtai Lab");
        hospitals.add("Hum Lab");
        hospitals.add("Life Line Lab");
        hospitals.add("Hormone Lab");
        hospitals.add("Al Nasar Laboratory");
        hospitals.add("Shaukat Khanum Laboratory Collection Centre");
        hospitals.add("Pioneer Lab");
        hospitals.add("Seven Laboratories & Diagnostic Centre");
        hospitals.add("Al Razi Lab");
        hospitals.add("Shifa Labs (Pickup Points)");
        hospitals.add("LIVARTES Pathology Lab");



        return hospitals;
    }




}











