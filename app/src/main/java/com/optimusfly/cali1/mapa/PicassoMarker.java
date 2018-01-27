package com.optimusfly.cali1.mapa;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by cali1 on 18/10/2017.
 */

public class PicassoMarker implements Target {
    Marker mMarker;

    public PicassoMarker(Marker marker) {
       this.mMarker = marker;
    }

    @Override
    public int hashCode() {
        return mMarker.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PicassoMarker) {
            Marker marker = ((PicassoMarker) o).mMarker;
            return mMarker.equals(marker);
        } else {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)         {

        if(bitmap!= null && from != null){
            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            mMarker.setVisible(true);
        }



    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }}