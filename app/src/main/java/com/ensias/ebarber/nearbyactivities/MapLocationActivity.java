package com.ensias.ebarber.nearbyactivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.ensias.ebarber.Common.Common;
import com.ensias.ebarber.MyServicesActivity;
import com.ensias.ebarber.R;
import com.ensias.ebarber.model.MarkerData;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryBounds;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapLocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private Marker addedMarker;
    private ExtendedFloatingActionButton floatingActionButton;
    private ExtendedFloatingActionButton floatingActionButtonquery;
    private LatLng currentLatLng;
    private final static double radiusInM = 5 * 1000;  // 5km
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArrayList<MarkerData> nearByLocations = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        initGui();
        setListener();
        receiveIntent();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();

    }

    private void initGui() {
        floatingActionButton = findViewById(R.id.fab_select_location);

    }

    private void setListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLatLng != null) {
                    final String saloonEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    saveCurrentLocation(currentLatLng, saloonEmail);

//                    saveLocation(currentLatLng, saloonEmail);
                }
            }
        });
    }

    private void receiveIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra(Common.OPEN_FOR).equals(Common.SELECT_LOCATION)) {
                floatingActionButton.setVisibility(View.VISIBLE);
            } else if (intent.getStringExtra(Common.OPEN_FOR).equals(Common.SHOW_NEAR_BY)) {
                floatingActionButton.setVisibility(View.GONE);

            }
        }
    }

    private void saveLocation(LatLng latLng, String saloonId) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://health-care-b9ce9-default-rtdb.firebaseio.com/").getReference("UserLocation");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(saloonId, new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                //Toast.makeText(MapLocationActivity.this,error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // queryLocation(latLng);

    }

    private void queryLocation(LatLng latLng) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://health-care-b9ce9-default-rtdb.firebaseio.com/").getReference("UserLocation");
        GeoFire geoFire = new GeoFire(ref);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 1);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Toast.makeText(MapLocationActivity.this, "Successfully Found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onKeyExited(String key) {
                Toast.makeText(MapLocationActivity.this, "left the place", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Toast.makeText(MapLocationActivity.this, "key moved but here", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(MapLocationActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCurrentLocation(LatLng latLng, String saloonId) {

        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latLng.latitude, latLng.longitude));

        // Add the hash and the lat/lng to the document. We will use the hash
        // for queries and the lat/lng for distance comparisons.
        Map<String, Object> updates = new HashMap<>();
        updates.put("geohash", hash);
        updates.put("lat", latLng.latitude);
        updates.put("lng", latLng.longitude);
        updates.put("title", Common.SALON_TITLE);
        updates.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        DocumentReference londonRef = db.collection("saloons").document(saloonId);
        londonRef.set(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //queryHashes(currentLatLng);
                        finish();

                    }

                });

        // [END fs_geo_add_hash]
    }

    //near by working fine,test by adding more users and then implement radius circle
    public static void queryHashes(LatLng centerLatLng, GoogleMap googleMap) {
        final GeoLocation center = new GeoLocation(centerLatLng.latitude, centerLatLng.longitude);

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("saloons")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);
            tasks.add(q.get());
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();
                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                double lat = doc.getDouble("lat");
                                double lng = doc.getDouble("lng");

                                // We have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match
                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                if (distanceInM <= radiusInM) {
                                    //matchingDocs.add(doc);
                                    nearByLocations.add(new MarkerData(lat, lng, 0.0, "", doc.getString("email")));
                                }
                            }
                        }

                        if (nearByLocations.size() > 0) {
                            addNearByMarkers(nearByLocations, googleMap);
                        }
                    }
                });
        // [END fs_geo_query_hashes]
    }

    private static void addNearByMarkers(ArrayList<MarkerData> markerDataArrayList, GoogleMap googleMap) {
        int i = 1;
        for (MarkerData marker : markerDataArrayList) {

            createMarker(marker.getLatitude(), marker.getLongitude(), i, googleMap);
            i++;
        }

    }

    protected static Marker createMarker(double latitude, double longitude, int pos, GoogleMap googleMap) {

        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .snippet(String.valueOf(pos))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                //    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapLocationActivity.this);
                } else {
                    startLocationUpdates();
                }
            }
        });
    }

    protected void startLocationUpdates() {
        if (!isLocationEnabled()) {
            showGPSNotEnabledDialog();
        }
        LocationRequest mLocationRequest;
        long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
        long FASTEST_INTERVAL = 2000; /* 2 sec */
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the
                    // result in onActivityResult().
                    ResolvableApiException rae = (ResolvableApiException) e;
                    rae.startResolutionForResult(MapLocationActivity.this, REQUEST_CODE);
                } catch (IntentSender.SendIntentException sie) {
                    //Log.i(TAG, "PendingIntent unable to execute request.");
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
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
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here

                        fetchLocation();
                        fusedLocationProviderClient.removeLocationUpdates(this);
                    }
                },
                Looper.myLooper());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Double lat, log;


        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().draggable(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        googleMap.setMyLocationEnabled(true);
        //        googleMap.addMarker(markerOptions).setDraggable(true);
        // MarkerOptions mo = new MarkerOptions();
        if (getIntent() != null && getIntent().getStringExtra(Common.OPEN_FOR).equals(Common.SHOW_NEAR_BY)) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLng).draggable(false);

        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLng).draggable(true);

        }
        addedMarker = googleMap.addMarker(markerOptions);
        if (getIntent() != null && getIntent().getStringExtra(Common.OPEN_FOR).equals(Common.SHOW_NEAR_BY)) {
            floatingActionButton.setVisibility(View.GONE);
        }

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                currentLatLng = googleMap.getCameraPosition().target;
                if (addedMarker != null) {
                    addedMarker.setPosition(currentLatLng);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                        return;
                    }
                    @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = location;
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                       //         Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap);
                                assert supportMapFragment != null;
                                supportMapFragment.getMapAsync(MapLocationActivity.this);
                            }
                        }
                    });
                }
                break;
        }
    }

    private void showAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapLocationActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Confirm");
        dialog.setMessage("Are you sure you want to cancel this order ?");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(MapLocationActivity.this, MyServicesActivity.class));
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "No".
                        dialog.dismiss();
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    public Boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    public void showGPSNotEnabledDialog() {
        //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
}
