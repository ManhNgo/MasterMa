package com.example.manhngo.mastermap;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.manhngo.mastermap.Modules.DirectionFinder;
import com.example.manhngo.mastermap.Modules.DirectionFinderListener;
import com.example.manhngo.mastermap.Modules.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, DirectionFinderListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    ControlMap controlMap;
    private Context context;

    private ProgressDialog progressDialog;

    Boolean isSearch;
    private FloatingActionButton fabMyLocation;
    private FloatingActionButton fabTraffic;
    private FloatingActionButton fabStuck;
    private FloatingActionButton fabMapType;
    private SearchBox search;

    String strMyLocation;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = getApplicationContext();

        fabMapType = (FloatingActionButton) findViewById(R.id.fab_MapType);
        fabMyLocation = (FloatingActionButton) findViewById(R.id.fab_MyLocation);
        fabTraffic = (FloatingActionButton) findViewById(R.id.fab_Traffic);
        fabStuck = (FloatingActionButton) findViewById(R.id.fab_Wanted);


        context = getApplicationContext();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        search = (SearchBox) findViewById(R.id.searchbox);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        search.enableVoiceRecognition(this);
        for(int x = 0; x < 10; x++){
            SearchResult option = new SearchResult("Result " + Integer.toString(x), getResources().getDrawable(R.drawable.ic_history));
            search.addSearchable(option);
        }
        search.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(MapsActivity.this, "Menu click", Toast.LENGTH_LONG).show();
            }

        });
        search.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged(String term) {
                Log.d("MA-onSearchTermChanged", term +" Searched");
                //React to the search term changing
                //Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {
                Log.d("MA-onSearch", searchTerm +" Searched");
                try {
                    new DirectionFinder(MapsActivity.this, strMyLocation, searchTerm).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
                Log.d("MA-onResultClick", result +" Searched");
            }

            @Override
            public void onSearchCleared() {
                //Called when the clear button is clicked
            }

        });
        search.setOverflowMenu(R.menu.overflow_menu);
        search.setOverflowMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.test_menu_item:
                        Toast.makeText(MapsActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
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
        mMap.setPadding(0, 300, 0, 0);


        controlMap = new ControlMap(mMap, context, MapsActivity.this);
        controlMap.updateMyLocation();
        controlMap.setFabMapType(fabMapType);
        controlMap.setFabMyLocation(fabMyLocation);
        controlMap.setFabTraffic(fabTraffic);
        controlMap.setFabStuck(fabStuck);
        controlMap.setControll();
        strMyLocation = getAdressStringFrom(10.7667782, 106.6621517);



        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(10.7667782, 106.6621517);
        Log.d("Geo", getAdressStringFrom(10.7667782, 106.6621517));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void mAddMarker(double v1, double v2 ){
        LatLng choosePlace = new LatLng(v1, v2);
        mMap.addMarker(new MarkerOptions().position(choosePlace).title("Marker in I go"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(choosePlace));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, results,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
        }
    }



    private String getAdressStringFrom(double latitude, double longitue){
        String strAddress = "";
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(latitude, longitue, 1);
            if(addresses != null){
                int i = 0;
                do{
                    strAddress = strAddress + addresses.get(0).getAddressLine(i);
                    i++;
                    if(i == addresses.get(0).getMaxAddressLineIndex()){
                        break;
                    }
                    strAddress = strAddress + ", ";
                }while (i < addresses.get(0).getMaxAddressLineIndex());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strAddress;
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}
