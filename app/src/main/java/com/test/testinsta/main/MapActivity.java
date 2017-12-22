package com.test.testinsta.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.testinsta.R;
import com.test.testinsta.db.GalleryDBModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qlooit-9 on 21/12/17.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_activity);
        intialControl();

    }

    private void intialControl() {

        setUpActionBar();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);


            mapFragment.getMapAsync(this);

        }

    }

    private ArrayList<String> getLatLong(List<GalleryDBModel> listData) {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            String lat = "";
            String lng = "";
            // Getting the latitude
            lat = listData.get(i).getLocation_lat();

            // Getting the longitude
            lng = listData.get(i).getLocation_long();
            if (!lat.equals("") && !lng.equals("0.0")) {
                data.add(lat + "#n#N#n#" + lng);
            }
        }
        return data;
    }

    private void setUpActionBar() {

        ((TextView) findViewById(R.id.txtTitle)).setText("Map Pins");
        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void drawMarker(LatLng point) {
        // Clears all the existing coordinates
//        googleMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title("Position");
        markerOptions.snippet("Latitude:" + point.latitude + ",Longitude" + point.longitude);
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        googleMap = map;
        List<GalleryDBModel> listData = GalleryDBModel.listAll(GalleryDBModel.class);
        ArrayList<String> total_data = getLatLong(listData);

        if (total_data.size() == 0) {
            Toast.makeText(this, "No Location base pin found.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            String lat = "";
            String lng = "";

            for (int i = 0; i < total_data.size(); i++) {

                // Getting the latitude
                lat = total_data.get(i).split("#n#N#n#")[0];

                // Getting the longitude
                lng = total_data.get(i).split("#n#N#n#")[1];

                // Drawing marker on the map
                drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("2")));
        }
    }

}
