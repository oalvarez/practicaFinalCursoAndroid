package com.example.BeThere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: AGMacBookOscar
 * Date: 23/05/13
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class NotificationService extends Service {

    private NotificationManager mNotificationManager = null;
    private Location mLocation = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service ", "Iniciando servicio");
        configGPS();
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        Log.d("Service ", "Finalizando servicio");
    }

    private void configGPS ()
    {
        LocationManager mLocationManager;
        LocationListener mLocationListener;
        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 15,
                mLocationListener);
    }

    private class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            //SE puede guardar la localización en el atributo aqui y utilizarlo en lugar de pasarlo como argumento
            Log.d("Location:", String.valueOf(location.getLatitude()) +
                    " " + String.valueOf(location.getLongitude()));
            mLocation = location;
            Geocoder gcd = new Geocoder(NotificationService.this, Locale.getDefault());
            String lastCity = getLastCityFromPreferences();
            Log.i("Last City","Last City visited" + lastCity);

            String currentCity;
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Log.i("Last City Stored", "Last City visited" + lastCity);
                if (addresses.size() > 0) {
                    currentCity = addresses.get(0).getLocality();
                    Log.i("current city", addresses.get(0).getLocality());
                    if (currentCity.equals("")||lastCity.equals(currentCity)){
                        Log.i("current city", "Last city is the same or null");
                    } else {
                        Log.i("New city", "New city visited!");
                        storeCurrentCityInPreferences(currentCity);
                        System.out.println(addresses.get(0).getLocality());
                        showNotification(currentCity);
                        addCityToVisited(currentCity);
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
/*
            List<String> visited = new ArrayList<String>();
            List<String> wanted = new ArrayList<String>();

            visited.add("Madrid");
            wanted.add("Rome");
            wanted.add("Paris");

            Gson gson = new Gson();
// This can be any object. Does not have to be an arraylist.
            String arrayString = gson.toJson(visited);
            Log.i("ArrayString", arrayString);
// How to retrieve your Java object back from the string
            List<String> obj = gson.fromJson(arrayString, ArrayList.class);
            obj.add("Rome");
            String arrayString2 = gson.toJson(obj);
            Log.i("ArrayString", arrayString2);
            */
        }

        private String getLastCityFromPreferences (){
            SharedPreferences preferences = getSharedPreferences("pref", 0);
            return preferences.getString("lastCity", "");
        }

        private void storeCurrentCityInPreferences (String currentCity) {
            SharedPreferences preferences = getSharedPreferences("pref", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("lastCity", currentCity);
            editor.commit();
        }

        public void addCityToVisited (String theCity) {
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
    private void showNotification(String theCity) {
        CharSequence text = this.getString(R.string.WelcomText) + theCity + "!";
        Notification notification = new Notification(R.drawable.bluepin, text, System.currentTimeMillis());
        Intent iNotification = new Intent(NotificationService.this, MainActivity.class);
        iNotification.putExtra("NOTIFY", true);
        iNotification.putExtra("LATITUDE", mLocation.getLatitude());
        iNotification.putExtra("LONGITUDE", mLocation.getLongitude());

        iNotification.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, iNotification, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, this.getString(R.string.NewCityvisited), text, contentIntent);

        mNotificationManager.notify(1,notification);
    }
}
