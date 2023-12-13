package com.cs407.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;

public class MapScreen extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private final LatLng mDestinationLatLng = new LatLng(43.0757339,-89.4065813);
    private GoogleMap mMap;
    private Map<String, LatLng> dict;
    private String store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        Intent intent = getIntent();
        store = intent.getStringExtra("storeName");

        dict = new HashMap<>();

        // Add keys and their latitudes and longitudes
        // TODO: Replace with the actual lats and longs of the nearest stores
        dict.put("Target", new LatLng(43.07491687419081, -89.45381764915044));
        dict.put("Amazon", new LatLng(43.072212858259995, -89.3973934663626)); // Sellery Locker/Hub
        dict.put("Walmart", new LatLng(43.04524903786975, -89.34883935820928));
        dict.put("Best Buy", new LatLng(43.06459252506946, -89.48360233689839));
        dict.put("UW Bookstore", new LatLng(43.07783251035348, -89.39817077385179)); // State Street

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            googleMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destination"));
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        displayMyLocation();
    }

    private void displayMyLocation() {
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, task -> {
                Location mLastKnownLocation = task.getResult();
                if(task.isSuccessful() && mLastKnownLocation != null){
                    mMap.addPolyline(new PolylineOptions().add(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), dict.get(store)));
                }
                SupportMapFragment mapFragment = (SupportMapFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                mapFragment.getMapAsync(googleMap -> {
                    mMap = googleMap;
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("Origin"));
                });
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }
    }

    /**
     * Deals with navigating back by one activity.
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home){
            navigateUpTo(new Intent(this, HomeScreen.class));
            return true;
        }
        return false;
    }
}