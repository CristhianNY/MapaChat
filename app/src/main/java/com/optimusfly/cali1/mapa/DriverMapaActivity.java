package com.optimusfly.cali1.mapa;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class DriverMapaActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;

    private Button mLogout;
    private String customerId= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverMapaActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        }else {
            mapFragment.getMapAsync(this);
        }

        mLogout = (Button) findViewById(R.id.logOut);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DriverMapaActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        getAssignedCustomer();

    }
    private  void  getAssignedCustomer(){

        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
      //  DatabaseReference assingCustomerRef = FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(driverId);
        DatabaseReference assingCustomerRef = FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(driverId).child("customerRideId");
        assingCustomerRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()){
            customerId = dataSnapshot.getValue().toString();
            getAssingnedCustomerPickupLocation();
          /*  Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();

            if(map.get("customerRideId")!= null){
                customerId = map.get("customerRideId").toString();
                getAssingnedCustomerPickupLocation();
            }**/
        }
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
    }


    private void getAssingnedCustomerPickupLocation(){
        DatabaseReference assingCustomerPickupLocation = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assingCustomerPickupLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>)dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationlong = 0;


                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationlong = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLalng = new LatLng(locationLat,locationlong);

               mMap.addMarker(new MarkerOptions().position(driverLalng).title("PickUp lOcation"));
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverMapaActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        }

        buildGoogleApiCliente();


        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiCliente(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverMapaActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

       if (getApplicationContext() != null){
           mLastLocation = location;

           LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
           //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
           //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


           String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
           DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driveravailable");
           DatabaseReference refworking = FirebaseDatabase.getInstance().getReference("driversWorking");
           GeoFire geoFireAvailable  = new GeoFire(refAvailable);
           GeoFire geoFirerefworking  = new GeoFire(refworking);

            switch (customerId){

                case "":
                    geoFirerefworking.removeLocation(userId);
                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;

                default:
                    geoFireAvailable.removeLocation(userId);
                    geoFirerefworking.setLocation(userId, new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;
            }
        }







    }


    @Override
    protected void onStop() {
        super.onStop();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driveravailable");

        GeoFire geoFire  = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driveravailable");

        GeoFire geoFire  = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                }else {

                    Toast.makeText(getApplicationContext(),"Por favor de permisos GPS", Toast.LENGTH_LONG).show();

                }
                break;
            }
        }
    }
}
