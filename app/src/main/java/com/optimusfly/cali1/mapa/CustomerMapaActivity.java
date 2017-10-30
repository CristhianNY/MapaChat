package com.optimusfly.cali1.mapa;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.ion.Ion;
import com.optimusfly.cali1.mapa.Models.Marcador;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class CustomerMapaActivity  extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;

    private LatLng pickupLocation;
    private Button mLogout, mRequest;
    private Marker mdriveMarker;

    private double radius = 1;
    private Boolean driverFound = false;
    private String driverFoundId;
    HashMap<Marker, Marcador> mDataMap = new HashMap<>();
    ArrayList<LatLng> locations = new ArrayList<LatLng>();
    //ArrayList<Marcador> markers = new ArrayList<>();
    private ArrayList<Marker> mMarkerArrayList;
    HashMap<String, Marker> hashMapMarker = new HashMap<>();
    List<Marker> mMarkers = new ArrayList<Marker>();
    private Map<String,Marker> markers;
    private String customerId= "";

    Marker m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapaActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        } else {
            mapFragment.getMapAsync(this);
        }

        mLogout = (Button) findViewById(R.id.logOut);
        mRequest = (Button) findViewById(R.id.callUber);
        markers = new HashMap<String, Marker>();
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Recogeme aqui"));
                mRequest.setText(" Getting your Driver");
                //   getClosestDriver();
            }
        });


        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapaActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }




    private void showDriverMoving() {

        // DatabaseReference assingCustomerPickupLocation = FirebaseDatabase.getInstance().getReference().child("driversWorking").child("EAWIK2RZphYJyrihvkdARhFLueH3").child("l");
        DatabaseReference refDriver = FirebaseDatabase.getInstance().getReference("userAvailable");

//
        //
        final GeoFire geoFireAvailable = new GeoFire(refDriver);
        final Set<String> runnersNearby = new HashSet<String>();
        final GeoLocation currentUserLocation = new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        final GeoQuery geoQuery = geoFireAvailable.queryAtLocation(currentUserLocation, radius);
       // Query locationQuery = FirebaseDatabase.getInstance().getReference().child("driveravailable");

    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(final String claveUser, final GeoLocation location) {
           // DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario").child(claveUser)
             //       .child("imagenPerfil");

            DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario");
            usuarioReferece.orderByChild("idUsuario").equalTo(claveUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot:
                            dataSnapshot.getChildren()
                            ) {

                        final Usuario userInfo = snapshot.getValue(Usuario.class);
                        try {
                            Bitmap bmImg = Ion.with(getApplicationContext())
                                    .load(userInfo.getImagenPerfil()).asBitmap().get();
                            LatLng meetLatLng = new LatLng(location.latitude, location.longitude);
                            Marker marker = markers.get(claveUser);
                            if (marker != null) {
                                marker.remove();
                                markers.remove(claveUser);
                            }



                            m =   mMap.addMarker(new MarkerOptions().position(meetLatLng).title(userInfo.getUsuario()));
                            m.setTag(userInfo.getIdUsuario());
                            PicassoMarker    mark = new PicassoMarker(m);
                            Picasso.with(getApplicationContext()).load(userInfo.getImagenPerfil()).resize(60,60).into(mark);

                            markers.put(claveUser,m);

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
                                @Override
                                public void onInfoWindowClick(Marker marker){


                                    Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                                    intent.putExtra("idUsuario",marker.getTag().toString());
                                    startActivity(intent);
                                    finish();

                                }
                            });



                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


// runnersNearby.add(claveUser);

//driverFound= true;
//driverFoundId = claveUser;
//getDriverLocation(claveUser);

// additional code, like displaying a pin on the map
// and adding Firebase listeners for this user
        }

        @Override public void onKeyExited(String key) {
           // runnersNearby.remove(username);
// additional code, like removing a pin from the map
// and removing any Firebase listener for this user

            Marker marker = markers.get(key);
            if (marker != null) {
                marker.remove();
                markers.remove(key);
            }

        }

        @Override public void onKeyMoved(String key, GeoLocation location) {

//por aca entra?

            Marker marker =markers.get(key);
            if (marker != null) {
             animateMarkerTo(marker, location.latitude, location.longitude);
            }


        }



        @Override public void onGeoQueryReady() {

        }

        @Override public void onGeoQueryError(DatabaseError error) {
            Toast.makeText(CustomerMapaActivity.this, "There was an error with this query " + error, Toast.LENGTH_SHORT).show();

        }


    });
    }

    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }



    /**

 geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
@Override public void onKeyEntered(final String claveUser, GeoLocation location) {


Query locationQuery = FirebaseDatabase.getInstance().getReference().child("driveravailable");

locationQuery.addValueEventListener(new ValueEventListener() {
@Override public void onDataChange(DataSnapshot dataSnapshot) {

for (DataSnapshot snap : dataSnapshot.getChildren()){

Double latitude = (Double) snap.child("l/0").getValue();
Double longitude = (Double) snap.child("l/1").getValue();
LatLng meetLatLng = new LatLng(latitude, longitude);

locations.add(meetLatLng);

Marker m =  mMap.addMarker(new MarkerOptions().position(meetLatLng));
hashMapMarker.put(claveUser,m);
}
}

@Override public void onCancelled(DatabaseError databaseError) {

}
});
// runnersNearby.add(claveUser);

//driverFound= true;
//driverFoundId = claveUser;
//getDriverLocation(claveUser);

// additional code, like displaying a pin on the map
// and adding Firebase listeners for this user
}

@Override public void onKeyExited(String username) {
runnersNearby.remove(username);
// additional code, like removing a pin from the map
// and removing any Firebase listener for this user
}

@Override public void onKeyMoved(String key, GeoLocation location) {


}

@Override public void onGeoQueryReady() {

}

@Override public void onGeoQueryError(DatabaseError error) {
Toast.makeText(CustomerMapaActivity.this, "There was an error with this query " + error, Toast.LENGTH_SHORT).show();

}

});
 **/





    private void getDriverLocation(final String id){
        DatabaseReference driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driveravailable").child(id).child("l");
        final DatabaseReference refMarcador = FirebaseDatabase.getInstance().getReference().child("marcadores");
        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>)dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationlong = 0;
                    mRequest.setText("Driver Found");

                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationlong = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLalng = new LatLng(locationLat,locationlong);

                    Location loc1 = new Location("");

                  //  loc1.setLatitude(pickupLocation.latitude);
                   // loc1.setLongitude(pickupLocation.longitude);
                   // mdriveMarker = mMap.addMarker(new MarkerOptions().position(driverLalng).title("you are driver"));
                    Location loc2 = new Location("");

                    loc2.setLatitude(driverLalng.latitude);
                    loc2.setLongitude(driverLalng.longitude);

                    Float distance = loc1.distanceTo(loc2);

                  //  mdriveMarker.remove();
                    Bitmap bm = BitmapFactory.decodeResource(Resources.getSystem(), android.R.drawable.ic_media_rew);
                    Marcador marcador = new Marcador(driverLalng,id,bm,id,driverLalng.latitude,driverLalng.longitude);

                    UsuariosTask userTask = new UsuariosTask();

                    userTask.execute(driverLalng.latitude,driverLalng.longitude,radius);




                    mRequest.setText("Driver Encontrado"+ String.valueOf(distance));




                 //   refMarcador.setValue(marcador);
                   // drawMarkers(markers);
                   // mdriveMarker = mMap.addMarker(new MarkerOptions().position(driverLalng).title(id));
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
            ActivityCompat.requestPermissions(CustomerMapaActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        }
        //mMap.addMarker(new MarkerOptions()
        //        .position(new LatLng(10, 10))
          //      .title("Hello world"));
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
            ActivityCompat.requestPermissions(CustomerMapaActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

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
        mLastLocation = location;
        if (getApplicationContext() != null){


        double latitude = location.getLatitude();

        showDriverMoving();



            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("userAvailable");
         //   DatabaseReference refworking = FirebaseDatabase.getInstance().getReference("driversWorking");
            GeoFire geoFireAvailable  = new GeoFire(refAvailable);
            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(),location.getLongitude()));
         //   GeoFire geoFirerefworking  = new GeoFire(refworking);


        }




    }

    @Override
    protected void onStop() {
        super.onStop();


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


    class UsuariosTask extends AsyncTask<Double,Integer,List<Marcador>> {

        @Override
        protected List<Marcador> doInBackground(Double... doubles) {

            //Conduct a network call will return a list o marcadores that are near me
            return null;
        }
        @Override
        protected void onPostExecute(List<Marcador> marcadors) {
            for (Marcador marcador:marcadors
                    ) {

                LatLng position = new LatLng(marcador.getLatitude(),marcador.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(marcador.getTitle()).snippet(marcador.getTitle()));

            }
        }
    }


}



