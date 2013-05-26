package com.example.BeThere;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.maps.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: AGMacBookOscar
 * Date: 23/05/13
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class MyMapActivity extends MapActivity {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private MapView mapview = null;
    private MapController mapController = null;

    private LinearLayout formuarioLayout = null;
    private EditText addcityEditText = null;
    ProgressDialog mProgress = null;
    Location location;

    @Override
    protected boolean isRouteDisplayed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);

        formuarioLayout = (LinearLayout) findViewById(R.id.formuarioLayout);
        addcityEditText = (EditText) findViewById(R.id.textoForumulario);

        Button goToLocation = (Button) findViewById(R.id.goToLocation);
        goToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshMap(location);
            }
        });
        Button addCity = (Button) findViewById(R.id.AddCity);

        addCity.setText(this.getString(R.string.AddButton));
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formuarioLayout.setVisibility(View.VISIBLE);
            }
        });
        Button goToVisitedCities = (Button) findViewById(R.id.BeenThere);

        goToVisitedCities.setText(this.getString(R.string.ListOfCitiesButton));
        goToVisitedCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent visitedCitiesIntent = new Intent(MyMapActivity.this, VisitedListActivity.class);
                startActivity(visitedCitiesIntent);
            }
        });
        Button addNewCity = (Button) findViewById(R.id.AddNewCity);
        addNewCity.setText(this.getString(R.string.AddCityButton));
        addNewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCityInText();
            }
        });
        //MyAsyncTask task = new MyAsyncTask();
        mProgress = ProgressDialog.show(this, getString(R.string.titleProgressDialog),this.getString(R.string.detailsProgressDialog));

        mapview = (MapView) findViewById(R.id.myMapView);
        mapview.setBuiltInZoomControls(true);
        mapController = mapview.getController();
        mapview.setClickable(true);
        //mapview.setTraffic(true);
        configGPS();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) refreshMap(location);
    }

    private void addCityInText () {
        formuarioLayout.setVisibility(View.INVISIBLE);
        Log.i("ciudad","La ciudad es " + addcityEditText.getText().toString());
        String theCity = addcityEditText.getText().toString();
        Gson gson = new Gson();
        SharedPreferences preferences = getSharedPreferences("pref", 0);
        String visitedCities = preferences.getString("visitedCities", " ");
        Log.i("ArrayString", "ciudades visitadas" + visitedCities);

        List<String> visited = gson.fromJson(visitedCities, ArrayList.class);
        List<String> visitedFirstTime = new ArrayList<String>();

        if (visited != null) {
            int count = visited.size();
            boolean alreadyVisited = false;
            Log.i("tamaño de lista", "tamaño de lista" +visited.size()+ visited);
            for (int i = 0; i < count;i++) {
                String city = visited.get(i);
                Log.i("Cities visited", city + " in position " + i);
                if (city.equals(theCity)) alreadyVisited = true;
            }
            if (alreadyVisited == false) visited.add(theCity);
        } else {
            visitedFirstTime.add(theCity);
            visited = visitedFirstTime;
        }
        visitedCities = gson.toJson(visited);
        Log.i("ArrayString", visitedCities);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("visitedCities", visitedCities);
        editor.commit();
    }

    private void configGPS ()
    {
        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 15,
                mLocationListener);
    }

    public class MyLocationListener implements LocationListener
    {
        private NotificationManager mNotificationManager;
        @Override
        public void onLocationChanged(Location location) {
            //SE puede guardar la localización en el atributo aqui y utilizarlo en lugar de pasarlo como argumento
            Log.d("Location:", String.valueOf(location.getLatitude()) +
                    " " + String.valueOf(location.getLongitude()));
            Geocoder gcd = new Geocoder(MyMapActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
            }
            refreshMap(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onProviderEnabled(String s) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onProviderDisabled(String s) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private void addWantedCity (){

    }

    private void addVisitedCity (){

    }

    private void refreshMap( Location location)
    {
        if (mProgress.isShowing()){
            mProgress.dismiss();
        }

        GeoPoint geoPoint = new GeoPoint ( (int) (location.getLatitude() * 1000000),
                (int) (location.getLongitude() * 1000000));

        mapController.setZoom(18);
        mapController.animateTo(geoPoint);

        MapOverlay myMapOverlay = new MapOverlay();
        myMapOverlay.setDrawable(getResources().getDrawable(R.drawable.googlepin));
        myMapOverlay.setGeoPoint(geoPoint);

        final List<Overlay> overlays = mapview.getOverlays();
        overlays.clear();

        overlays.add(myMapOverlay);
    }
}
