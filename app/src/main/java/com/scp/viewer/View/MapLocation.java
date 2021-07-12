/*
  ClassName: MapLocation.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class MapLocation use to view location on google map.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.PolylineOptions;
import com.scp.viewer.Database.DatabaseGetLocation;
import com.scp.viewer.Model.GPS;
import com.scp.viewer.R;
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
import static com.scp.viewer.API.APIDatabase.formatDate;
import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT_MMM;
import static com.scp.viewer.API.Global.DEFAULT_TIME_FORMAT_AM;
import static com.scp.viewer.API.Global.DEFAULT_TIME_START;
import static com.scp.viewer.API.Global.MAPTYPE;
import static com.scp.viewer.API.Global.SETTINGS;
import static com.scp.viewer.Adapter.AdapterHistoryLocation.getAddress;
import static com.scp.viewer.View.HistoryLocation.mData;

public class MapLocation extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    List<GPS> gpsListShowMap;
    List<String>gpsDateList;
    private Toolbar toolbar;
    private CircleImageView cimg_SelectMapHistory;
    LinearLayout ln_Map_Type_History_Select;
    private ImageView img_back_History_Location,img_next_History_Location, img_Map_History_Street_Map,img_Map_History_Terrain,img_Map_History_Satellite;
    private TextView txt_Time_History_Location, txt_Close_History_SelectMap, txt_Normal_History_Map, txt_Terrain_History_Map,txt_Satellite_History_Add;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String dateSelected = "0";
    private DatabaseGetLocation databaseGetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme_NoActionBar);
        gpsListShowMap = new ArrayList<>();
        gpsDateList = new ArrayList<>();
        setContentView(R.layout.activity_history_map_locations);
        sharedPreferences = getApplicationContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        databaseGetLocation = new DatabaseGetLocation(getApplicationContext());
        setID();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        img_back_History_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numberDateNow = getPositionOfDateSelected();
                dateSelected = gpsDateList.get(numberDateNow-1);
                mMap.clear();
                setMarker();
            }
        });

        img_next_History_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numberDateNow = getPositionOfDateSelected();
                dateSelected = gpsDateList.get(numberDateNow+1);
                mMap.clear();
                setMarker();
            }
        });
    }

    private void setID() {

        toolbar = findViewById(R.id.toolbar_History_Location);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.LOCATION_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        //for crate home button
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

        getListDate(mData);
        setMarker();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                View v = getLayoutInflater().inflate(R.layout.activity_marker, null);
                TextView info = v.findViewById(R.id.txt_Info_Location);
                TextView timeLocation = v.findViewById(R.id.txt_Time_Location);
                timeLocation.setText(marker.getSnippet());
                info.setText(marker.getTitle());
                return v;
            }
        });
    }

    private void getListDate(List<GPS> mData) {
        if(mData.size()>0)
        {
            try {
                String dateTamp = "2012-01-01";
                for(int i=0; i<mData.size();i++)
                {
                    if(!dateTamp.contains(formatDate(mData.get(i).getClient_GPS_Time(), DEFAULT_DATE_FORMAT)))
                    {
                        dateTamp = formatDate(mData.get(i).getClient_GPS_Time(), DEFAULT_DATE_FORMAT);
                        gpsDateList.add(0,dateTamp);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMarker()
    {
        GPS gps = (GPS) getIntent().getSerializableExtra("GPS");
        String time_Date_Location;
        if(dateSelected.equals("0"))
            time_Date_Location = getTimeItem(gps.getClient_GPS_Time(), DEFAULT_DATE_FORMAT_MMM);
        else
            time_Date_Location = getTimeItem(dateSelected + DEFAULT_TIME_START, DEFAULT_DATE_FORMAT_MMM);
        Log.d("dateGPS", "dateGPS = "+dateSelected+ " = = "+ time_Date_Location);
        txt_Time_History_Location.setText(time_Date_Location);

        try {

            if(dateSelected.equals("0"))
                dateSelected = formatDate(gps.getClient_GPS_Time(), DEFAULT_DATE_FORMAT);
            Log.d("dateGPS", "dateGPS = "+ dateSelected);
        } catch (ParseException e) {
            if(dateSelected.equals("0"))
                dateSelected = gps.getClient_GPS_Time().substring(0,10);
            e.printStackTrace();
        }

        setGoneIMGBackAndNext();
        //2020-08-26 08:20:00
        String date = gps.getClient_GPS_Time();
        gpsListShowMap.clear();
        List<GPS> gpsListFromDate = databaseGetLocation.getAll_Location_ID_History_Date(gps.getDevice_ID(),dateSelected);
        for (GPS mGPS:gpsListFromDate)
        {
            if(mGPS.getClient_GPS_Time().contains(dateSelected))
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

                if(gps.getClient_GPS_Time().contains(dateSelected))
                {
                    if(gpsListShowMap.get(i).getRowIndex() == gps.getRowIndex())
                    {
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
                else {
                    if(i == (gpsListShowMap.size()-1))
                    {
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
        }
    }

    /**
     * setIMGBackAndNext is a method of checking how many different dates the list of positions has and handling.
     */
    private void setGoneIMGBackAndNext() {
        if(gpsDateList.size()<=1)
        {
            img_back_History_Location.setVisibility(View.INVISIBLE);
            img_next_History_Location.setVisibility(View.INVISIBLE);
        }
        else
        {
            img_back_History_Location.setVisibility(View.VISIBLE);
            img_next_History_Location.setVisibility(View.VISIBLE);
        }

        getPositionOfDateSelected();
    }

    private int getPositionOfDateSelected() {
        int positionDate = 0;

        for (int i = 0; i<gpsDateList.size(); i++)
        {
            if(gpsDateList.get(i).equals(dateSelected))
            {
                positionDate = i;
            }
        }
        if(gpsDateList.size() == 1)
        {
            img_back_History_Location.setVisibility(View.INVISIBLE);
            img_next_History_Location.setVisibility(View.INVISIBLE);
        }
        else {

            if(positionDate == gpsDateList.size()-1)
            {

                img_back_History_Location.setVisibility(View.VISIBLE);
                img_next_History_Location.setVisibility(View.INVISIBLE);
            }

            if(positionDate == 0)
            {
                img_back_History_Location.setVisibility(View.INVISIBLE);
                img_next_History_Location.setVisibility(View.VISIBLE);
            }
        }

        return positionDate;
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
