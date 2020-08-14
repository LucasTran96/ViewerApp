/*
  ClassName: MapLocation.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class MapLocation use to view location on google map.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.jexpa.secondclone.Model.GPS;
import com.jexpa.secondclone.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        setMarker();
    }

    public void setMarker() {
        GPS gps = (GPS) getIntent().getSerializableExtra("GPS");
        String mAddresses = getIntent().getStringExtra("Addresses");
        if(mAddresses.equals("Location not found!")|| mAddresses.equals("null"))
        {
            LatLng sydney = new LatLng(gps.getLatitude(), gps.getLongitude());
            Log.d("LatLng", gps.getLatitude() + "==" + gps.getLongitude());
            //Toast.makeText(this, gps.getLatitude()+"=="+gps.getLongitude(), Toast.LENGTH_SHORT).show();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.draggable(true);
            markerOptions.position(sydney);
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(sydney)
                    .bearing(3.5f)
                    .tilt(45)
                    .zoom(15).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

            mMap.animateCamera(cameraUpdate, 1000, null);


        }else {
            String listAddresses [] = mAddresses.split(",");
            LatLng sydney = new LatLng(gps.getLatitude(), gps.getLongitude());
            Log.d("LatLng", gps.getLatitude() + "==" + gps.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.draggable(true);
            try
            {
                if(listAddresses.length>0)
                {
                    if(listAddresses[0] != null && listAddresses[1] != null)
                    {
                        markerOptions.title(listAddresses[0]+","+listAddresses[1]);
                    }else {
                        markerOptions.title(mAddresses.replace("null",""));
                    }
                    if(listAddresses[1] != null
                            && listAddresses[2] != null
                            && listAddresses[3] != null
                            && (!listAddresses[1].equals(" null")))
                    {
                        markerOptions.snippet(listAddresses[1] + "," + listAddresses[2] + "," + listAddresses[3]);
                    }else {
                        markerOptions.snippet("Unknown");
                    }
                }
            }catch (Exception e)
            {
                markerOptions.title(mAddresses.replace(" null",""));
                markerOptions.snippet(mAddresses.replace(" null",""));
                e.getMessage();
            }

            markerOptions.position(sydney);
            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.aaaa)));
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
            //Toast.makeText(this, gps.getLatitude()+"=="+gps.getLongitude(), Toast.LENGTH_SHORT).show();
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(sydney)
                    .bearing(2.5f)
                    .tilt(45)
                    .zoom(15).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(cameraUpdate, 900, null);

        }

    }



}
