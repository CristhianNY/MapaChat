package com.optimusfly.cali1.mapa.Services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cali1 on 4/12/2017.
 */

public class GpsTraker extends Service implements LocationListener {

    private final Context mContext;

    boolean isGpsEnable = false;
    boolean isNetworkEnable = false;
    boolean canGetLocation = false;

    Location mLastLocation;
    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1;
    protected LocationManager locationManager;

    public GpsTraker(Context mContext) {
        this.mContext = mContext;
        getLocation();

    }





    private Location getLocation() {

        try {

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            isGpsEnable= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGpsEnable && !isNetworkEnable){


            }else{
                this.canGetLocation = true;
                if(isNetworkEnable){
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                  && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_FOR_UPDATES,this);

                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if(location!= null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if(isGpsEnable){
                    if(location == null){
                         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_FOR_UPDATES,this);
                         if(locationManager != null){
                             location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                             if(location != null){
                                 latitude = location.getLatitude();
                                 longitude = location.getLongitude();
                             }
                         }
                    }

                }
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,this);
        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }


    public void stopUsingGps(){
        if(locationManager != null){

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                return;
            }

                locationManager.removeUpdates(GpsTraker.this);
        }
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return  latitude;
    }
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return  longitude;
    }

    public boolean canGetLocation(){

        return this.canGetLocation;

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showSettingsAlert(){

        AlertDialog.Builder alerDialog = new AlertDialog.Builder(mContext);

        alerDialog.setTitle("Configuracion GPS");
        alerDialog.setMessage("El gps esta dehabilitado , Deseas activar el GPS ?");

        alerDialog.setPositiveButton("Configurarción", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alerDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // mostrando dialogo
        alerDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("userAvailable");
        //   DatabaseReference refworking = FirebaseDatabase.getInstance().getReference("driversWorking");
        GeoFire geoFireAvailable = new GeoFire(refAvailable);

        geoFireAvailable.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        Toast.makeText(getApplicationContext(), "My Position !!!"+ mLastLocation.getLatitude() + mLastLocation.getLongitude(),
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
