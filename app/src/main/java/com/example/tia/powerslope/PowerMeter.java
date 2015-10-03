package com.example.tia.powerslope;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tia on 9/13/15.
 */
public class PowerMeter {

    private class GpsInterval {
        public double interval;
        public double distance;
        public double[] positionAccuracy = new double[2];
        public double distanceAccuracy;
        public double altitudeDifference;
        public double altitudeDifferenceAccuracy;
    }

    private GpsInterval gpsInterval = new GpsInterval();
    private double slope, angle;
    private double slopeAccuracy, angleAccuracy;
    private double velocity;
    private double velocityAccuracy;
    private UserProfile userProfile = new UserProfile();
    private double powerTotal;

    public boolean setGpsInterval(double interval, double distance) {
        if(interval <= 0 || distance <= 0)
            return false;
        this.gpsInterval.interval = interval;
        this.gpsInterval.distance = distance;
        return true;
    }
    public void setGpsPositionAccuracy(double positionAccuracy0, double positionAccuracy1) {
        this.gpsInterval.positionAccuracy[0] = positionAccuracy0;
        this.gpsInterval.positionAccuracy[1] = positionAccuracy1;
        this.setGpsDistanceAccuracy();
        this.setGpsVelocity();
    }
    public void setGpsDistanceAccuracy() {
        this.gpsInterval.distanceAccuracy = Math.sqrt( Math.pow(this.gpsInterval.positionAccuracy[0],2) +
                                           Math.pow(this.gpsInterval.positionAccuracy[1],2) );
        this.setGpsVelocityAccuracy();
    }
    public void setGpsAltitudeDifference(double altitudeDifference) {
        this.gpsInterval.altitudeDifference = altitudeDifference;
        setGpsSlope();
    }
    public void setGpsSlope() {
        this.slope = this.gpsInterval.altitudeDifference / this.gpsInterval.distance;
        this.angle = Math.asin(this.slope);
    }
    public void setGpsSlopeAccuracy() {
        this.gpsInterval.altitudeDifferenceAccuracy = Math.sqrt(Math.pow(this.slope*this.gpsInterval.distanceAccuracy,2)); //TODO: is this mathematically correct?
        this.slopeAccuracy = Math.sqrt(Math.pow(this.gpsInterval.altitudeDifferenceAccuracy / this.gpsInterval.distanceAccuracy, 2) +
                Math.pow(this.gpsInterval.distanceAccuracy / Math.pow(this.gpsInterval.distance, 2), 2));
    }
    public void setGpsVelocity() {
        this.velocity = this.gpsInterval.distance / gpsInterval.interval;
    }
    public void setGpsVelocityAccuracy() {
        this.velocityAccuracy = this.gpsInterval.distanceAccuracy / gpsInterval.interval;
    }
    public void setUserProfile(JSONObject userProfileJson) {
        this.userProfile.fromJson(userProfileJson);
    }
    public void computePowerTotal() {
        userProfile.createTestUser("Tia");
        this.powerTotal = 1.0;
    }

    public double getGpsInterval() {
        return this.gpsInterval.interval;
    }
    public double getGpsDistance() {
        return this.gpsInterval.distance;
    }
    public double getDistanceAccuracy() {
        return this.gpsInterval.distanceAccuracy;
    }
    public double getAltitudeDifference() {
        return this.gpsInterval.altitudeDifference;
    }
    public double getAltitudeDifferenceAccuracy() {
        return this.gpsInterval.altitudeDifferenceAccuracy;
    }
    public double getSlope() {
        return this.slope;
    }
    public double getSlopeAccuracy() {
        return this.slopeAccuracy;
    }
    public double getVelocity() {
        return this.velocity;
    }
    public double getVelocityAccuracy() {
        return this.velocityAccuracy;
    }
    public UserProfile getUserProfile() {
        return userProfile;
    }
    public double getPowerTotal() {
        return this.powerTotal;
    }
}
