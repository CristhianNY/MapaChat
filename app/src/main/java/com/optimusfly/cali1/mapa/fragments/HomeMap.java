package com.optimusfly.cali1.mapa.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.optimusfly.cali1.mapa.LoginActivity;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.PicassoMarker;
import com.optimusfly.cali1.mapa.R;
import com.optimusfly.cali1.mapa.Services.GPSService;
import com.optimusfly.cali1.mapa.Services.GpsTraker;
import com.squareup.picasso.Picasso;

import junit.framework.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMap extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private SupportMapFragment mSupportMapFragment;
    private Map<String, Marker> markers;
    GoogleApiClient mGoogleApiClient;
    Marker m;
    boolean GpsStatus;
    private Button controlGps,prenderGps;
    ImageView btnCurrentLocation;

    private Set<GeoQuery> geoQueries = new HashSet<>();
    View v;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    private double radius = 900000;
    private boolean hideMark = false;

    public HomeMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        markers = new HashMap<String, Marker>();
        //  mMapView.getMapAsync(this);

        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser !=null){
            if (v != null) {
                ViewGroup parent = (ViewGroup) v.getParent();
                if (parent != null)
                    parent.removeView(v);
            }
            try {
                v = inflater.inflate(R.layout.fragment_home_map, container, false);
                mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapHome);
                mSupportMapFragment.getMapAsync(this);



            } catch (InflateException e) {
        /* map is already there, just return view as it is */
            }

            controlGps = (Button) v.findViewById(R.id.controlGPS);
            prenderGps = (Button) v.findViewById(R.id.prederGPS);
            btnCurrentLocation = (ImageView) v.findViewById(R.id.currentLocation);

            btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    checkGpsStatus();
                    if(GpsStatus != true){

                        AlertDialog.Builder alerDialog = new AlertDialog.Builder(getContext());

                        alerDialog.setTitle("Configuracion GPS");
                        alerDialog.setMessage("Ocultar Loacalización?");

                        alerDialog.setPositiveButton("Configurarción", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                getContext().startActivity(intent);


                            }
                        });

                        alerDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alerDialog.show();

                    }else{
                        GetPositionTask fetchCordinates = new GetPositionTask(getActivity()) {

                            @Override
                            protected void onPostExecute(Double[] result) {

                                if (result != null) {
                                    double latitude = result[0];
                                    double longitude = result[1];

                                    // have coordinates, continue on UI thread
                                    //   textViewLocation.setText(latitude + "/" + longitude);
                                    LatLng l = new LatLng(latitude, longitude);
                                    CameraPosition position = new CameraPosition.Builder()
                                            .target(l) // Sets the new camera position
                                            .zoom(27) // Sets the zoom
                                            .bearing(0) // Rotate the camera
                                            .tilt(80) // Set the camera tilt
                                            .build(); // Creates a CameraPosition from the builder
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition((position)));
                                } else {
                                    // error occurred
                                }
                            }


                        };
                        fetchCordinates.execute();
                    }

                }
            });
            prenderGps.setVisibility(View.GONE);


            prenderGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    prenderGps.setVisibility(View.GONE);
                    controlGps.setVisibility(View.VISIBLE);
                    hideMark = false;
                }
            });
            controlGps.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {




                   checkGpsStatus();
                   if(GpsStatus == true){
                       AlertDialog.Builder alerDialog = new AlertDialog.Builder(getContext());

                       alerDialog.setTitle("Configuracion GPS");
                       alerDialog.setMessage("Ocultar Loacalización?");

                       alerDialog.setPositiveButton("Configurarción", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                               getContext().startActivity(intent);


                           }
                       });

                       alerDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                           }
                       });

                       // mostrando dialogo
                       checkGpsStatus();

                       if(GpsStatus== false){
                           controlGps.setVisibility(View.GONE);
                           prenderGps.setVisibility(View.VISIBLE);
                       }
                       alerDialog.show();

                   }





                }
            });
            return v;

        }else{

            Intent myIntent = new Intent( getActivity(), LoginActivity.class);
            startActivityForResult(myIntent, 0);

        }
     return v;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiCliente();

        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!= null) {


            checkGpsStatus();
            if (GpsStatus != false) {


            GetPositionTask fetchCordinates = new GetPositionTask(getActivity()) {

                @Override
                protected void onPostExecute(Double[] result) {

                    if (result != null) {
                        double latitude = result[0];
                        double longitude = result[1];

                        // have coordinates, continue on UI thread
                        //   textViewLocation.setText(latitude + "/" + longitude);
                    } else {
                        // error occurred
                    }
                }

            };


            fetchCordinates.execute();

        }else{
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final  DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("userAvailable").child(userId);
                ref2.removeValue();


            }

            MostrarUsuarios mostrarUsuarios = new MostrarUsuarios();
            mostrarUsuarios.execute();
        }else{
            Intent myIntent = new Intent( getActivity(), LoginActivity.class);
            startActivityForResult(myIntent, 0);
        }

        //     textViewLocation.setText(latitude+""+longitude);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);
    }

    final int LOCATION_REQUEST_CODE = 1;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(9000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        }


        // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiCliente() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onStart() {
        super.onStart();
        buildGoogleApiCliente();

        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!= null){

        }else{
            Intent myIntent = new Intent( getActivity(), LoginActivity.class);
            startActivityForResult(myIntent, 0);
        }
    }
    abstract class GetPositionTask extends AsyncTask<Void, Void,  Double[]> implements LocationListener
    {


        final long TWO_MINUTES = 2*60*1000;
        private Location location;
        private LocationManager lm;
        LatLng latLng;
        public GetPositionTask(Context context) {
            lm = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
        }
        protected void onPreExecute()
        {
            // Configure location manager - I'm using just the network provider in this example
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 9000, 0, this);
           // nearProgress.setVisibility(View.VISIBLE);
        }

        protected  Double[] doInBackground(Void... params)
        {

            if(GpsStatus != false){

            }
            Double[] coords = null;
            // Try to use the last known position
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            if(GpsStatus !=false){
            Location lastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


            // If it's too old, get a new one by location manager
            if (System.currentTimeMillis() - lastLocation.getTime() > TWO_MINUTES)
            {
                while (location == null)
                    try { Thread.sleep(100); } catch (Exception ex) {}

                return coords;
            }

            coords = new Double[2];
            coords[0] = lastLocation.getLatitude();
            coords[1] = lastLocation.getLongitude();
            }
            return coords;
        }

        protected void onPostExecute(Location location)
        {
           // nearProgress.setVisibility(View.GONE);
            lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            lm.removeUpdates(this);

            // HERE USE THE LOCATION
        }
        boolean isFirstLocation=false;

        @Override
        public void onLocationChanged(Location newLocation)
        {
            location = newLocation;
            FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();

            if(currentUser != null){
                if (getActivity() != null) {
                     latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if(!isFirstLocation)
                    {
                        CameraPosition position = new CameraPosition.Builder()
                                .target(latLng) // Sets the new camera position
                                .zoom(27) // Sets the zoom
                                .bearing(0) // Rotate the camera
                                .tilt(80) // Set the camera tilt
                                .build(); // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition((position)));


                        isFirstLocation=true;
                    }
                }
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("userAvailable");
                //   DatabaseReference refworking = FirebaseDatabase.getInstance().getReference("driversWorking");
                GeoFire geoFireAvailable = new GeoFire(refAvailable);

                geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
            }



        }

        public void onProviderDisabled(String provider) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final  DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("userAvailable").child(userId);
            ref2.removeValue();

            AlertDialog.Builder alerDialog = new AlertDialog.Builder(getContext());

            alerDialog.setTitle("Configuracion GPS");
            alerDialog.setMessage("Ocultar Loacalización?");

            alerDialog.setPositiveButton("Configurarción", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getContext().startActivity(intent);


                }
            });

            alerDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alerDialog.show();
        }
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        protected abstract void onPostExecute(Double[] result);

    }






    public class MostrarUsuarios extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // DatabaseReference assingCustomerPickupLocation = FirebaseDatabase.getInstance().getReference().child("driversWorking").child("EAWIK2RZphYJyrihvkdARhFLueH3").child("l");
            DatabaseReference refDriver = FirebaseDatabase.getInstance().getReference("userAvailable");

//
            //
            final GeoFire geoFireAvailable = new GeoFire(refDriver);

            GPSService gpsTraker = new GPSService(getActivity());


            final GeoLocation currentUserLocation = new GeoLocation(gpsTraker.getLatitude(), gpsTraker.getLongitude());
            final GeoQuery geoQuery = geoFireAvailable.queryAtLocation(currentUserLocation, radius);


            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(final String claveUser, final GeoLocation location) {

                    DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario");
                    usuarioReferece.orderByChild("idUsuario").equalTo(claveUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot:
                                    dataSnapshot.getChildren()
                                    ) {

                                final Usuario userInfo = snapshot.getValue(Usuario.class);


                                LatLng meetLatLng = new LatLng(location.latitude, location.longitude);

                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Marker marker = markers.get(claveUser);
                                Marker marker2 = markers.get(userId);

                                if(hideMark == true && marker2 != null){

                                     marker2.remove();
                                     markers.remove(userId);

                                }
                                if (marker != null) {
                                    marker.remove();
                                    markers.remove(claveUser);
                                }

                                if(userInfo.getIdUsuario().length() != 0){

                               m =   mMap.addMarker(new MarkerOptions().position(meetLatLng).title(userInfo.getUsuario()).visible(false));
                                    MarkerOptions marcador = new MarkerOptions().position(
                                            meetLatLng).title(userInfo.getUsuario());


                                m.setTag(userInfo.getIdUsuario());



                                    PicassoMarker mark = new PicassoMarker(m);
                                    markers.put(claveUser,m);

                                  /**  Glide.with(getActivity()).load(userInfo.getImagenPerfil())
                                            .asBitmap().fitCenter().override(50,50).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                                            m.setIcon(icon);
                                            m.setVisible(true);
                                        }
                                    });**/
                                  if(mark!=null){
                                      Picasso.with(getActivity()).load(userInfo.getImagenPerfil()).resize(60,60).centerCrop().transform(new CropCircleTransformation()).into(mark);

                                  }




                                }


                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
                                    @Override
                                    public void onInfoWindowClick(Marker marker){




                                        Bundle bundle = new Bundle();
                                        bundle.putString("idUsuario",marker.getTag().toString());


                                        PerfilFragment perfilFragment = new PerfilFragment();
                                        perfilFragment.setArguments(bundle);

                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                                        transaction.replace(R.id.container, perfilFragment);
                                        transaction.addToBackStack(null);

// Commit the transaction
                                        transaction.commit();

                                    }
                                });





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
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Marker marker2 = markers.get(userId);

                    if(hideMark == true && marker2 != null){

                        marker2.remove();
                        markers.remove(userId);

                    }
                    Marker marker =markers.get(key);
                    if (marker != null) {

                        animateMarkerTo(marker, location.latitude, location.longitude);
                        mMap.getUiSettings().setMapToolbarEnabled(false);
                    }


                }



                @Override public void onGeoQueryReady() {

                }

                @Override public void onGeoQueryError(DatabaseError error) {
                    Toast.makeText(getActivity(), "There was an error with this query " + error, Toast.LENGTH_SHORT).show();

                }


            });
            geoQueries.add(geoQuery);
            return null;
        }


        public void animateMarkerTo(final Marker marker, final double lat, final double lng) {

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

    }
    private void  checkGpsStatus(){


        locationManager = (LocationManager)getContext().getSystemService(getContext().LOCATION_SERVICE);
        GpsStatus  = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);



    }

}
