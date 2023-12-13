package com.cs407.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapScreen extends FragmentActivity {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private final LatLng mDestinationLatLng = new LatLng(43.0757339,-89.4065813);
    private GoogleMap mMap;
    private Map<String, LatLng> dict;
    private ArrayList<String> stores;
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
        dict.put("UW Bookstore", new LatLng(43.07467277962888, -89.3978017859309)); // State Street

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            //googleMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destination"));
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
                String minStore = "";
                // Create Location objects from LatLng points
                Location location1 = new Location("point1");
                location1.setLatitude(mLastKnownLocation.getLatitude());
                location1.setLongitude(mLastKnownLocation.getLongitude());

                Location location2 = new Location("point2");
                location2.setLatitude(dict.get(stores.get(0)).latitude);
                location2.setLongitude(dict.get(stores.get(0)).longitude);

                // Calculate distance between the two locations (in meters)
                float distance = location1.distanceTo(location2);

                double minStoreDist = distance;
                for(String store: stores){
                    int lineColor = Color.BLACK;
                    if(task.isSuccessful() && mLastKnownLocation != null){
                        if(store.equals("UW Bookstore")){
                            lineColor = Color.RED;
                        }
                        if(store.equals("Best Buy")){
                            lineColor = Color.BLUE;
                        }
                        if(store.equals("Amazon")){
                            lineColor = Color.YELLOW;
                        }
                        if(store.equals("Target")){
                            lineColor = Color.RED;
                        }
                        if(store.equals("Walmart")){
                            lineColor = Color.BLUE;
                        }

                        mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), dict.get(store)))
                                .setColor(lineColor);

                        location1 = new Location("point1");
                        location1.setLatitude(mLastKnownLocation.getLatitude());
                        location1.setLongitude(mLastKnownLocation.getLongitude());

                        location2 = new Location("point2");
                        location2.setLatitude(dict.get(store).latitude);
                        location2.setLongitude(dict.get(store).longitude);

                        // Calculate distance between the two locations (in meters)
                        distance = location1.distanceTo(location2);
                        if(distance < minStoreDist){
                            minStoreDist = distance;
                            minStore = store;
                        }
                    }



                }

                TextView display = findViewById(R.id.mapDetails);
                display.setText("Closest store is " + minStore + ", which is " + minStoreDist +"m away.");

                SupportMapFragment mapFragment = (SupportMapFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                mapFragment.getMapAsync(googleMap -> {
                    mMap = googleMap;
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("Origin"));

                    // Create a CameraPosition
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("Origin").getPosition())
                            .zoom(15) // Set the desired zoom level
                            .build();

                    // Move the camera to the marker position with animation
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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