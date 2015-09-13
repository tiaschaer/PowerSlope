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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class RealTimePower extends AppCompatActivity {

    private static final double msTOs = 1./1000.;
    private static final double mPsTOkmPh = 3.6;
    private static final double realTime_averagingInterval = 10000; // [ms]
    private static final double realTime_updateInterval = 1000; // [ms]
    private static final int realTime_valuesNum = (int) Math.floor(realTime_averagingInterval/realTime_updateInterval) + 1;
    private int realTime_bufferInd = -1;
    private Location[] realTime_location = new Location[realTime_valuesNum];
    private PowerMeter pm = new PowerMeter();

    private TextView twStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_power);

        this.twStatus = (TextView) findViewById(R.id.tw_statusBar);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) realTime_updateInterval, 0, locationListener);

        return true;
    }

    public void makeUseOfNewLocation(Location location) {

        read_location_current(location);

        boolean updateRealTimeAveragedValues = update_location_buffer(location);

        if(updateRealTimeAveragedValues)
            read_location_buffer();
    }

    private void read_location_current(Location location) {
        String strNotAvailable = getString(R.string.notAvailable);
        TextView twTmp;
        twTmp = (TextView) findViewById(R.id.tw_time);
        //twTmp.setText(String.valueOf((double)location.getTime() * millisecondsToSeconds));
        twTmp.setText(utc_to_realTime(location.getTime()));
        twTmp = (TextView) findViewById(R.id.tw_latitude);
        twTmp.setText(String.valueOf(location.getLatitude()));
        twTmp = (TextView) findViewById(R.id.tw_longitude);
        twTmp.setText(String.valueOf(location.getLongitude()));
        twTmp = (TextView) findViewById(R.id.tw_accuracy);
        if(location.hasAccuracy()) {
            twTmp.setText(String.valueOf(location.getAccuracy()));
        }
        else {
            twTmp.setText(strNotAvailable);
        }
        twTmp = (TextView) findViewById(R.id.tw_altitude);
        if(location.hasAltitude()) {
            twTmp.setText(String.valueOf(location.getAltitude()));
        }
        else {
            twTmp.setText(strNotAvailable);
        }
        twTmp = (TextView) findViewById(R.id.tw_speed);
        if(location.hasSpeed()) {
            twTmp.setText(String.valueOf(location.getSpeed()));
        }
        else {
            twTmp.setText(strNotAvailable);
        }
    }

    private boolean update_location_buffer(Location location) {
        // shift older values
        for(int ii = this.realTime_bufferInd; ii >= 0; --ii)
            if(ii < realTime_valuesNum-1)
                this.realTime_location[ii+1] = this.realTime_location[ii];
        // store new value
        this.realTime_location[0] = location;
        if (this.realTime_bufferInd < realTime_valuesNum-1 )
            ++this.realTime_bufferInd;
        // find point for averaging
        boolean realTimeAverageAvailable = false;
        int point2Ind = 0;
        while(++point2Ind <= this.realTime_bufferInd)
            if(this.realTime_location[0].getTime() - this.realTime_location[point2Ind].getTime() - realTime_averagingInterval >= 0) {
                realTimeAverageAvailable = true;
                this.realTime_bufferInd = point2Ind;
                break;
            }
        return realTimeAverageAvailable;
    }

    private void read_location_buffer() {
        // DEBUGGING
        this.twStatus.setText(String.valueOf(this.realTime_bufferInd) +
                "/(" + utc_to_realTime(this.realTime_location[this.realTime_bufferInd].getTime()) +
                "," + utc_to_realTime(this.realTime_location[0].getTime()) + ")" );
        // update values in GUI
        boolean pointOK;
        pointOK = pm.setRtPoint(msTOs * (double)(this.realTime_location[0].getTime() - this.realTime_location[this.realTime_bufferInd].getTime()),
                                      this.realTime_location[this.realTime_bufferInd].distanceTo(this.realTime_location[0]));
        //TextView twTmp = (TextView) findViewById(R.id.tw_distancePoint1);
        //twTmp.setText(String.valueOf(distancePoint2));
        if(pointOK) {
            ((TextView) findViewById(R.id.tw_intervalPoint1)).setText(String.valueOf(pm.getRtInterval()));
            ((TextView) findViewById(R.id.tw_distancePoint1)).setText(String.valueOf(pm.getRtDistance()));
        }
        else {
            ((TextView) findViewById(R.id.tw_intervalPoint1)).setText(R.string.notAvailable);
            ((TextView) findViewById(R.id.tw_distancePoint1)).setText(R.string.notAvailable);
        }
        boolean availablePositionAccuracy = this.realTime_location[0].hasAccuracy() &&
                                         this.realTime_location[this.realTime_bufferInd].hasAccuracy();
        if (availablePositionAccuracy) {
            pm.setRtPositionAccuracy(this.realTime_location[0].getAccuracy(),
                                     this.realTime_location[this.realTime_bufferInd].getAccuracy());
            ((TextView)findViewById(R.id.tw_distancePoint1_error)).setText(String.valueOf(pm.getDistanceAccuracy()));
            ((TextView)findViewById(R.id.tw_speedPoint1)).setText(String.valueOf(pm.getVelocity() * mPsTOkmPh));
            ((TextView)findViewById(R.id.tw_speedPoint1_error)).setText(String.valueOf(pm.getVelocityAccuracy()*mPsTOkmPh));
        }

    }

    private String utc_to_realTime(long utcTime) {
        Date date = new Date(utcTime);
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat_realTime));
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

}
