package com.example.BeThere;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startService(new Intent(this, NotificationService.class));

        Button goToMap = (Button) findViewById(R.id.goToMap);
        goToMap.setText(this.getString(R.string.goToMapButton));
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(MainActivity.this, MyMapActivity.class);
                startActivity(mapIntent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
           //To change body of overridden methods use File | Settings | File Templates.
        Intent i = getIntent();

        Boolean notify = i.getBooleanExtra("NOTIFY", false);
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(!notify)
        {
            Log.i("NOTIFICATION INFO","Ejecuci'on lanzada de aplicacion");
        }
        else {
            double latitude = i.getDoubleExtra("LATITUDE", -1);
            double longitude = i.getDoubleExtra("LONGITUDE", -1);
            Log.i("NOTIFICATION INFO","Ejecuci'on desde el manager de las notificaciones de android"+ "Nueva localizaci'on" +latitude + longitude);
        }
        mNotificationManager.cancel(1);
    }
}
