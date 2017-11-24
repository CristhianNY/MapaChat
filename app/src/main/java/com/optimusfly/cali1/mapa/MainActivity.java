package com.optimusfly.cali1.mapa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.MapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.optimusfly.cali1.mapa.fragments.ChatFragment;
import com.optimusfly.cali1.mapa.fragments.ChatListFragment;
import com.optimusfly.cali1.mapa.fragments.ListUserNearFragment;
import com.optimusfly.cali1.mapa.fragments.MapaFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{

    private Button cliente,driver;
    final int LOCATION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private Boolean permisos = false;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("test");
       // String token = SharedPrefManager.getInstance(this).getDeviceToken();
       // Log.d(TAG,"TOKEN "+ FirebaseInstanceId.getInstance().getToken().toString());
       // Log.d(TAG,"TOKEN cris"+ token);



        String id = getIntent().getStringExtra("id");

        if(id != null){
            Bundle bundle = new Bundle();

            bundle.putString("id",id);


            ChatListFragment chatListFragment = new ChatListFragment();
            chatListFragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.container,chatListFragment).commit();

        }else{
            MapaFragment mapaFragment = new MapaFragment();
            //  MainFragment mainFragment = new MainFragment();
            // principalFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mapaFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                 //   mapFragment.getMapAsync(this);

                    permisos = true;
                }else {

                    Toast.makeText(getApplicationContext(),"Por favor de permisos GPS", Toast.LENGTH_LONG).show();

                }
                break;
            }
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();
        if(id == R.id.mapa){
            //Do whatever you want to do
            MapaFragment mapaFragment = new MapaFragment();

            //  MainFragment mainFragment = new MainFragment();
            // principalFragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();

            manager.beginTransaction().replace(R.id.container,mapaFragment).commit();




        }

        if(id == R.id.chat){
            Bundle bundle = new Bundle();

            bundle.putString("id","null");

            ChatListFragment chatListFragment = new ChatListFragment();
            chatListFragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.container,chatListFragment).commit();
        }

        if(id == R.id.salir){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Estas seguro que quieres salir?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "Saliendo ",Toast.LENGTH_LONG).show();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userAvailable");

                            GeoFire geoFire  = new GeoFire(ref);
                            geoFire.removeLocation(userId);
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);

                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
