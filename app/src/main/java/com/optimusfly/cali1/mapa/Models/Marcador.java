package com.optimusfly.cali1.mapa.Models;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cali1 on 10/10/2017.
 */

public class Marcador {
    LatLng latLng;
    String title;
    Bitmap bitmap;
    String id ;

    private double latitude,longitude;


    public Marcador() {

    }
    public Marcador(LatLng latLng, String title, Bitmap bitmap, String id, double latitude, double longitude) {
        this.latLng = latLng;
        this.title = title;
        this.bitmap = bitmap;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}