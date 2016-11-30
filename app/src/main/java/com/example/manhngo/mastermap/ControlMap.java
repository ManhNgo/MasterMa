package com.example.manhngo.mastermap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.quinny898.library.persistentsearch.SearchBox;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

/**
 * Created by Manh Ngo on 11/30/2016.
 */

public class ControlMap extends Activity{


    private GoogleMap mMap;
    private Context context;
    private Activity activity;

    private FloatingActionButton fabMyLocation;
    private FloatingActionButton fabTraffic;
    private FloatingActionButton fabStuck;
    private FloatingActionButton fabMapType;
    private LatLng myLatLng;

    private boolean myLocation = true;
    private boolean myTraffic = false;
    private boolean myStuck = false;
    private boolean myMapType = true; // true: normall || false: satelite

    GPSTracker gpsTracker;

    Boolean isSearch;
    private SearchBox search;


    private boolean mShowPermissionDeniedDialog = false;
    private LocationManager locationManager;
    private LocationListener locationListener;

    ControlMap(GoogleMap googleMap, Context context, Activity activity){
        this.mMap = googleMap;
        this.context = context;
        this.activity = activity;
    }

    public void setFabMyLocation(FloatingActionButton fabMyLocation) {
        this.fabMyLocation = fabMyLocation;
    }

    public void setFabTraffic(FloatingActionButton fabTraffic) {
        this.fabTraffic = fabTraffic;
    }

    public void setFabStuck(FloatingActionButton fabStuck) {
        this.fabStuck = fabStuck;
    }

    public void setFabMapType(FloatingActionButton fabMapType) {
        this.fabMapType = fabMapType;
    }


    public void setControll(){
        fabMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMapType = !myMapType;
                updateMapType();
            }
        });
        fabMyLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Snackbar.make(view, "My location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                myLocation = !myLocation;
                updateMyLocation();

            }
        });
        fabTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Traffic", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                myTraffic = !myTraffic;
                updateTraffic();
            }
        });
        fabStuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStuck = !myStuck;
                updateLocationStuck();
            }
        });

    }


    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(context, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void updateMyLocation() {
        if (!checkReady()) {
            return;
        }

        if (!myLocation) {
            mMap.setMyLocationEnabled(false);
            Log.d("Load traffic","ACB");
            return;
        }

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            gpsTracker = new GPSTracker(context, activity);
            if(gpsTracker.canGetLocation()){
                Log.d("MapsActivity", "canGetLocation");
                Log.d("MA-Location",
                        "la" + gpsTracker.getLatitude() + ", " + gpsTracker.getLongitude());

                myLatLng = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
            }else{
                Log.d("MapsActivity", "can't get location");
            }
            Log.d("Set Coordinate","ACB");
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            myLocation = false;
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.INTERNET,
                    }, 10);

        }
    }

    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        mMap.setTrafficEnabled(myTraffic);
    }

    private void updateMapType() {
        // No toast because this can also be called by the Android framework in onResume() at which
        // point mMap may not be ready yet.
        if(!checkReady()){
            return;
        }
        if (myMapType) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else{
            mMap.setMapType(MAP_TYPE_SATELLITE);
        }

    }

    private void updateLocationStuck() {
        if (!checkReady()) {
            return;
        }
        if (myStuck) {
            if(myLatLng != null){
                Log.d("GPS", "Chua dinh vi");
            }else{
                mMap.addCircle(new CircleOptions()
                        .center(myLatLng)
                        .radius(50)
                        .strokeWidth(0)
                        .strokeColor(0x00ff0000)
                        .fillColor(0x7f000000)
                        .clickable(true));
            }
        }

    }
}


