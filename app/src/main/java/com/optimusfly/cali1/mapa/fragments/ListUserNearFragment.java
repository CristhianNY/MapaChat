package com.optimusfly.cali1.mapa.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.optimusfly.cali1.mapa.Adapters.UserListAdapter;
import com.optimusfly.cali1.mapa.Dibujos.BubbleTransformation;
import com.optimusfly.cali1.mapa.Models.Marcador;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.Models.UsuarioCerca;
import com.optimusfly.cali1.mapa.PicassoMarker;
import com.optimusfly.cali1.mapa.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListUserNearFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private UserListAdapter adapter;

    private RecyclerView recyclerView;
    Location mLastLocation;


    private double radius = 1;
    GoogleApiClient mGoogleApiClient;


    View v;
    private List<UsuarioCerca> usuariosCerca;

    public ListUserNearFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }

        try {
            v = inflater.inflate(R.layout.fragment_list_user_near, container, false);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


        recyclerView = (RecyclerView) v.findViewById(R.id.usuarios_cerca);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        usuariosCerca = new ArrayList<>();
        buildGoogleApiCliente();
        return v;
    }


    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (getActivity() != null) {


            double latitude = location.getLatitude();

            mostrarUsuariosCerca();


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("userAvailable");
            //   DatabaseReference refworking = FirebaseDatabase.getInstance().getReference("driversWorking");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
        }

    }

    protected synchronized void buildGoogleApiCliente() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    private void mostrarUsuariosCerca() {


        // DatabaseReference assingCustomerPickupLocation = FirebaseDatabase.getInstance().getReference().child("driversWorking").child("EAWIK2RZphYJyrihvkdARhFLueH3").child("l");
        DatabaseReference refDriver = FirebaseDatabase.getInstance().getReference("userAvailable");

//
        //
        final GeoFire geoFireAvailable = new GeoFire(refDriver);

        final GeoLocation currentUserLocation = new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        final GeoQuery geoQuery = geoFireAvailable.queryAtLocation(currentUserLocation, radius);
        // Query locationQuery = FirebaseDatabase.getInstance().getReference().child("driveravailable");

        adapter = new UserListAdapter(usuariosCerca, this);
        recyclerView.setAdapter(adapter);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String claveUser, final GeoLocation location) {
                // DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario").child(claveUser)
                //       .child("imagenPerfil");

                final DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario");
                usuarioReferece.orderByChild("idUsuario").equalTo(claveUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //usuariosCerca.removeAll(usuariosCerca);
                        for (DataSnapshot snapshot :
                                dataSnapshot.getChildren()
                                ) {


                            final UsuarioCerca userInfo = snapshot.getValue(UsuarioCerca.class);


                            LatLng meetLatLng = new LatLng(location.latitude, location.longitude);


                            usuariosCerca.add(userInfo);


                        }
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        adapter.notifyDataSetChanged();
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


            @Override
            public void onKeyExited(String key) {
                // runnersNearby.remove(username);
// additional code, like removing a pin from the map
// and removing any Firebase listener for this user


            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

//por aca entra?


            }


            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(getActivity(), "There was an error with this query " + error, Toast.LENGTH_SHORT).show();

            }


        });
    }
    final int LOCATION_REQUEST_CODE = 1;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mostrarUsuariosCerca();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
