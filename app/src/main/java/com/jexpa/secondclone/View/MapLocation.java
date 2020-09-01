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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jexpa.secondclone.API.APIDatabase.checkValueStringT;
import static com.jexpa.secondclone.API.APIDatabase.formatDate;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.Global.DEFAULT_DATE_FORMAT;
import static com.jexpa.secondclone.API.Global.DEFAULT_DATE_FORMAT_MMM;
import static com.jexpa.secondclone.API.Global.DEFAULT_TIME_FORMAT_AM;
import static com.jexpa.secondclone.API.Global.MAPTYPE;
import static com.jexpa.secondclone.API.Global.SETTINGS;
import static com.jexpa.secondclone.Adapter.AdapterHistoryLocation.getAddress;
import static com.jexpa.secondclone.View.HistoryLocation.mData;

public class MapLocation extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    List<GPS> gpsListShowMap;
    private Toolbar toolbar;
    private CircleImageView cimg_SelectMapHistory;
    LinearLayout ln_Map_Type_History_Select;
    private ImageView img_back_History_Location,img_next_History_Location, img_Map_History_Street_Map,img_Map_History_Terrain,img_Map_History_Satellite;
    private TextView txt_Time_History_Location, txt_Close_History_SelectMap, txt_Normal_History_Map, txt_Terrain_History_Map,txt_Satellite_History_Add;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme_NoActionBar);
        gpsListShowMap = new ArrayList<>();
        setContentView(R.layout.activity_history_map_locations);
        sharedPreferences = getApplicationContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        toolbar = findViewById(R.id.toolbar_History_Location);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.LOCATION_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        //for crate home button
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setID();
        // setEvent() is a method for assigning events to the user's button.
        setEvent();
    }


    private void setEvent() {
        // cimg_SelectMapHistory is the button that handles the choice of the map type that the user wants to see.
        cimg_SelectMapHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ln_Map_Type_History_Select.setVisibility(View.VISIBLE);
                if(sharedPreferences.getInt(MAPTYPE,1) == 1)
                {
                    txt_Normal_History_Map.setTextColor(getResources().getColor(R.color.borderMap));
                    img_Map_History_Street_Map.setBackground(getResources().getDrawable(R.drawable.custom_background_select_map));
                    txt_Satellite_History_Add.setTextColor(getResources().getColor(R.color.black));
                    img_Map_History_Satellite.setBackgroundColor(getResources().getColor(R.color.mapselect));
                    txt_Terrain_History_Map.setTextColor(getResources().getColor(R.color.black));
                    img_Map_History_Terrain.setBackgroundColor(getResources().getColor(R.color.mapselect));
                }

                else if(sharedPreferences.getInt(MAPTYPE,1) == 2)
                {
                    txt_Normal_History_Map.setTextColor(getResources().getColor(R.color.black));
                    img_Map_History_Street_Map.setBackgroundColor(getResources().getColor(R.color.mapselect));
                    txt_Satellite_History_Add.setTextColor(getResources().getColor(R.color.borderMap));
                    img_Map_History_Satellite.setBackground(getResources().getDrawable(R.drawable.custom_background_select_map));
                    txt_Terrain_History_Map.setTextColor(getResources().getColor(R.color.black));
                    img_Map_History_Terrain.setBackgroundColor(getResources().getColor(R.color.mapselect));

                }
                else
                {
                    txt_Normal_History_Map.setTextColor(getResources().getColor(R.color.black));
                    img_Map_History_Street_Map.setBackgroundColor(getResources().getColor(R.color.mapselect));
                    txt_Satellite_History_Add.setTextColor(getResources().getColor(R.color.black));
                    img_Map_History_Satellite.setBackgroundColor(getResources().getColor(R.color.mapselect));
                    txt_Terrain_History_Map.setTextColor(getResources().getColor(R.color.borderMap));
                    img_Map_History_Terrain.setBackground(getResources().getDrawable(R.drawable.custom_background_select_map));
                }
            }
        });

        img_Map_History_Terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mMap!= null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    editor.putInt(MAPTYPE,3);
                    editor.apply();
                    ln_Map_Type_History_Select.setVisibility(View.GONE);
                }
            }
        });
        img_Map_History_Street_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mMap!= null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    editor.putInt(MAPTYPE,1);
                    editor.apply();
                    ln_Map_Type_History_Select.setVisibility(View.GONE);
                }
            }
        });
        img_Map_History_Satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mMap!= null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    ln_Map_Type_History_Select.setVisibility(View.GONE);
                    editor.putInt(MAPTYPE,2);
                    editor.apply();
                }
            }
        });

        txt_Close_History_SelectMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln_Map_Type_History_Select.setVisibility(View.GONE);
            }
        });
    }

    private void setID() {
        // toolbar_History_Location
        cimg_SelectMapHistory = findViewById(R.id.cimg_SelectMapHistory);
        ln_Map_Type_History_Select = findViewById(R.id.ln_Map_Type_History_Select);
        ln_Map_Type_History_Select.setVisibility(View.GONE);
        img_Map_History_Street_Map = findViewById(R.id.img_Map_History_Street_Map);
        img_Map_History_Terrain = findViewById(R.id.img_Map_History_Terrain);
        img_Map_History_Satellite = findViewById(R.id.img_Map_History_Satellite);
        txt_Close_History_SelectMap = findViewById(R.id.txt_Close_History_SelectMap);
        txt_Normal_History_Map = findViewById(R.id.txt_Normal_History_Map);
        txt_Terrain_History_Map = findViewById(R.id.txt_Terrain_History_Map);
        txt_Satellite_History_Add = findViewById(R.id.txt_Satellite_History_Add);
        cimg_SelectMapHistory = findViewById(R.id.cimg_SelectMapHistory);
        txt_Time_History_Location = findViewById(R.id.txt_Time_History_Location);
        img_back_History_Location = findViewById(R.id.img_back_History_Location);
        img_next_History_Location = findViewById(R.id.img_next_History_Location);
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

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.activity_marker, null);
                TextView info = v.findViewById(R.id.txt_Info_Location);
                TextView timeLocation = v.findViewById(R.id.txt_Time_Location);
                timeLocation.setText(marker.getSnippet());
                info.setText(marker.getTitle());
                return v;
            }
        });
    }

    public void setMarker() {
        GPS gps = (GPS) getIntent().getSerializableExtra("GPS");
        String mAddresses = getIntent().getStringExtra("Addresses");
        String time_URL = getTimeItem(checkValueStringT( gps.getClient_GPS_Time()), DEFAULT_DATE_FORMAT_MMM);
        txt_Time_History_Location.setText(time_URL);
        String dateGPS;
        try {
            dateGPS = formatDate(gps.getClient_GPS_Time(), DEFAULT_DATE_FORMAT);
        } catch (ParseException e) {
            dateGPS = gps.getClient_GPS_Time().substring(0,10);
            e.printStackTrace();
        }

        //2020-08-26 08:20:00
        String date = gps.getClient_GPS_Time();
        for (GPS mGPS:mData)
        {
            if(mGPS.getClient_GPS_Time().contains(dateGPS))
            {
                gpsListShowMap.add(mGPS);
            }
        }

        List<LatLng> latLngList = new ArrayList<>();
        for (int z = 0; z < gpsListShowMap.size(); z++) {
            LatLng point = new LatLng(gpsListShowMap.get(z).getLatitude(),gpsListShowMap.get(z).getLongitude());
            latLngList.add(point);
        }
        if(latLngList.size()>1)
        {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.RED);
            polyOptions.width(8);
            polyOptions.geodesic(false);
            polyOptions.addAll(latLngList);
            mMap.addPolyline(polyOptions);
        }

        for (int i = 0 ; i < gpsListShowMap.size(); i++)
        {
                final LatLng sydney = new LatLng(gpsListShowMap.get(i).getLatitude(), gpsListShowMap.get(i).getLongitude());
                final int finalI = i;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mMap.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .title(getAddress(gpsListShowMap.get(finalI).getLatitude(), gpsListShowMap.get(finalI).getLongitude(),getApplicationContext())+"")
                                    .snippet(formatDate(gpsListShowMap.get(finalI).getClient_GPS_Time(),DEFAULT_TIME_FORMAT_AM)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                if(gpsListShowMap.get(i).getRowIndex() == gps.getRowIndex())
                {
//                    WaitingDialog.dismissDialog();
//                    hideProgressDialog();
                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(sydney)
                            .bearing(2.5f)
                            .tilt(45)
                            .zoom(14)
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.animateCamera(cameraUpdate, 900, null);
                }
        }


        Log.d("LatLng", "date = "+ date +" getLatitude = " +gps.getLatitude() + "==" + gps.getLongitude());
      /*  if(mAddresses.equals("Location not found!")|| mAddresses.equals("null"))
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


        }
        else {
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
*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home) {
                super.onBackPressed();
        }
        return true;
    }


}
