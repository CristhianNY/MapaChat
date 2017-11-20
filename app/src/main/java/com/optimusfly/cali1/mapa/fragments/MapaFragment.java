package com.optimusfly.cali1.mapa.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.koushikdutta.ion.Ion;
import com.optimusfly.cali1.mapa.CustomerMapaActivity;
import com.optimusfly.cali1.mapa.Dibujos.BubbleTransformation;
import com.optimusfly.cali1.mapa.Models.Marcador;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.PerfilActivity;
import com.optimusfly.cali1.mapa.PicassoMarker;
import com.optimusfly.cali1.mapa.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;

    private LatLng pickupLocation;
    private Button mLogout, mRequest;
    private Marker mdriveMarker;

    private double radius = 500;
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
    MapView mMapView;
    private GoogleMap googleMap;
    private SupportMapFragment mSupportMapFragment;
    Marker m;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private final static String TAG = "DashBoardActivity";
    private int mContainerId;
    private Set<GeoQuery> geoQueries = new HashSet<>();

    View v;
    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        markers = new HashMap<String, Marker>();
      //  mMapView.getMapAsync(this);

        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.fragment_mapa, container, false);
            mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mSupportMapFragment.getMapAsync(this);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return v;


    }
    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        }
        //mMap.addMarker(new MarkerOptions()
        //        .position(new LatLng(10, 10))
        //      .title("Hello world"));

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_chat, menu);
       // inflater.inflate(R.menu.usuarios_cerca_menu, menu);
       // Use filter.xml from step 1
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.usuarios){
            //Do whatever you want to do

            removeListener();
            ListUserNearFragment listUserNearFragment = new ListUserNearFragment();


            FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
            transaction.replace(R.id.container, listUserNearFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();
            return true;
        }
        if(id == R.id.listChat){

            ChatListFragment chatListFragment = new ChatListFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container,chatListFragment);
            transaction.addToBackStack(null);

            transaction.commit();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    boolean isFirstLocation=false;

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
        if (getActivity() != null) {


            double latitude = location.getLatitude();

            showDriverMoving();


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(!isFirstLocation)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 19.0F));
                isFirstLocation=true;
            }
           // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
            //mMap.animateCamera(CameraUpdateFactory.);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("userAvailable");
                //   DatabaseReference refworking = FirebaseDatabase.getInstance().getReference("driversWorking");
                GeoFire geoFireAvailable = new GeoFire(refAvailable);


                geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
            }

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        }
        //mMap.addMarker(new MarkerOptions()
        //        .position(new LatLng(10, 10))
        //      .title("Hello world"));
        buildGoogleApiCliente();
        mMap.setMyLocationEnabled(true);

    //    mMap.setMyLocationEnabled(true);

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


                                LatLng meetLatLng = new LatLng(location.latitude, location.longitude);


                                Marker marker = markers.get(claveUser);
                                if (marker != null) {
                                    marker.remove();
                                    markers.remove(claveUser);
                                }



                                m =   mMap.addMarker(new MarkerOptions().position(meetLatLng).title(userInfo.getUsuario()));
                                m.setTag(userInfo.getIdUsuario());
                                PicassoMarker mark = new PicassoMarker(m);
                                Picasso.with(getActivity()).load(userInfo.getImagenPerfil()).resize(60,60).centerCrop().transform(new CropCircleTransformation()).into(mark);

                                markers.put(claveUser,m);

                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
                                    @Override
                                    public void onInfoWindowClick(Marker marker){



                                       /** Intent intent = new Intent(getActivity(), PerfilActivity.class);
                                        intent.putExtra("idUsuario",marker.getTag().toString());
                                        startActivity(intent);
                                       getActivity().finish();**/
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

                Marker marker =markers.get(key);
                if (marker != null) {

                   animateMarkerTo(marker, location.latitude, location.longitude);
                }


            }



            @Override public void onGeoQueryReady() {

            }

            @Override public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(getActivity(), "There was an error with this query " + error, Toast.LENGTH_SHORT).show();

            }


        });
        geoQueries.add(geoQuery);
    }

    private void  removeListener(){

        for(GeoQuery geoQuery: geoQueries){

            geoQuery.removeAllListeners();


        }


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

    protected synchronized void buildGoogleApiCliente(){

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }



    @Override
    public void onPause() {
        super.onPause();
        removeListener();
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null){
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userAvailable");

            GeoFire geoFire  = new GeoFire(ref);
            geoFire.removeLocation(userId);
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeListener();
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null){
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }



    }




}
