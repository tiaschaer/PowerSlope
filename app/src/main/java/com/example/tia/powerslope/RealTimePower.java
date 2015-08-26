package com.example.tia.powerslope;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class RealTimePower extends AppCompatActivity {

    private static double secondToMillisecond = 1000.0;
    private static double realTime_averagingInterval = 10.0; // [s]
    private static double realTime_updateInterval = 1.0; // [s]
    private static int realTime_valuesNum = (int) Math.ceil(realTime_averagingInterval/realTime_updateInterval);
    private int realTime_bufferInd = 0;
    private Location[] realTime_location = new Location[realTime_valuesNum];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_power);

        final Button but_startListening = (Button) findViewById(R.id.but_startListening);
        but_startListening.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getLocation();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_real_time_power, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getLocation() {
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the LocationManager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        return true;
    }

    public void makeUseOfNewLocation(Location location) {
        // Update raw data table
        TextView tTmp;
        tTmp = (TextView) findViewById(R.id.tw_latitude);
        tTmp.setText(String.valueOf(location.getLatitude()));
        tTmp = (TextView) findViewById(R.id.tw_longitude);
        tTmp.setText(String.valueOf(location.getLongitude()));
        tTmp = (TextView) findViewById(R.id.tw_accuracy);
        if(location.hasAccuracy()) {
            tTmp.setText(String.valueOf(location.getAccuracy()));
        }
        else {
            tTmp.setText("@strings/notAvailable");
        }
        tTmp = (TextView) findViewById(R.id.tw_altitude);
        if(location.hasAltitude()) {
            tTmp.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tTmp.setText("@strings/notAvailable");
        }
        tTmp = (TextView) findViewById(R.id.tw_speed);
        if(location.hasSpeed()) {
            tTmp.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tTmp.setText("@strings/notAvailable");
        }
        // Update buffer
        if(realTime_bufferInd == 0)
            this.realTime_location[0] = location;
        else if(location.getTime() > this.realTime_location[realTime_bufferInd].getTime()+secondToMillisecond*realTime_updateInterval) {
            if (this.realTime_bufferInd < realTime_valuesNum - 1)
                this.realTime_location[++this.realTime_bufferInd] = location;
            else {
                for (int ii = 1; ii < realTime_valuesNum; ++ii) {
                    this.realTime_location[ii - 1] = this.realTime_location[ii];
                    this.realTime_location[realTime_valuesNum - 1] = location;
                }
            }
        }
        // Update computed data table
        if(this.realTime_bufferInd == realTime_valuesNum-1) {
            double distancePoint1 = this.realTime_location[realTime_valuesNum - 1].distanceTo(this.realTime_location[0]);
            tTmp = (TextView) findViewById(R.id.tw_distancePoint1);
            tTmp.setText(String.valueOf(distancePoint1));
        }
    }
}
