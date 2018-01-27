package com.optimusfly.cali1.mapa.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cali1 on 18/12/2017.
 */

public class Marca {

private String imagenPerfil;
private LatLng ubicación ;
private String Usuario;



    public Marca(){

    }
    public Marca(String imagenPerfil, LatLng ubicación) {
        this.imagenPerfil = imagenPerfil;
        this.ubicación = ubicación;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public LatLng getUbicación() {
        return ubicación;
    }

    public void setUbicación(LatLng ubicación) {
        this.ubicación = ubicación;
    }
}
