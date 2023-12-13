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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MapScreen Activity displays a Google Map with routes from the user's location to selected stores.
 * It utilizes the FusedLocationProviderClient to obtain the user's current location and Google Maps API
 * for displaying the map and routes. Store locations and names are passed through an Intent from the
 * calling activity.
 */
public class MapScreen extends FragmentActivity {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private final LatLng mDestinationLatLng = new LatLng(43.0757339,-89.4065813);
    private GoogleMap mMap;
    private Map<String, LatLng> dict;
    private ArrayList<String> stores;

    /**
     * Called when the activity is first created. Initializes the map, location services, and displays
     * routes from the user's location to selected stores.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        Intent intent = getIntent();
        stores = intent.getStringArrayListExtra("stores");

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

    /**
     * Displays the user's current location and draws routes from the user's location to selected stores.
     */
    private void displayMyLocation() {
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, task -> {
                Location mLastKnownLocation = task.getResult();

                for(String store: stores){
                    if(task.isSuccessful() && mLastKnownLocation != null){
                        mMap.addPolyline(new PolylineOptions().add(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), dict.get(store)));
                    }
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

    /**
     * Callback for the result from requesting permissions. Handles the result of the request for location
     * permissions and calls displayMyLocation() if permissions are granted.
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int).
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED
     *                     or PERMISSION_DENIED. Never null.
     */
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
     * @return Returns true if the navigation was handled, false otherwise.
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