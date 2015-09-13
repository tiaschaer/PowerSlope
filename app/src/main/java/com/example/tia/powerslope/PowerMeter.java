package com.example.tia.powerslope;


/**
 * Created by tia on 9/13/15.
 */
public class PowerMeter {

    private class GpsInterval {
        public double interval;
        public double distance;
        public double[] positionAccuracy = new double[2];
    }

    private GpsInterval point = new GpsInterval();
    private double distanceAccuracy;
    private double velocity;
    private double velocityAccuracy;


//    public PowerMeter PowerMeter(double time1, double position1, double time2, double position2) {
//        position1 =
//    }

    public boolean setRtPoint(double interval, double distance) {
        if(interval <= 0 || distance <= 0)
            return false;
        this.point.interval = interval;
        this.point.distance = distance;
        return true;
    }
    public void setRtPositionAccuracy(double positionAccuracy0, double positionAccuracy1) {
        this.point.positionAccuracy[0] = positionAccuracy0;
        this.point.positionAccuracy[1] = positionAccuracy1;
        this.setDistanceAccuracy();
        this.setVelocity();
    }
    public void setDistanceAccuracy() {
        this.distanceAccuracy = Math.sqrt( Math.pow(this.point.positionAccuracy[0],2) +
                                           Math.pow(this.point.positionAccuracy[1],2) );
        this.setVelocityAccuracy();
    }
    public void setVelocity() {
        this.velocity = this.point.distance / point.interval;
    }
    public void setVelocityAccuracy() {
        this.velocityAccuracy = this.distanceAccuracy / point.interval;
    }

    public double getRtInterval() {
        return this.point.interval;
    }
    public double getRtDistance() {
        return this.point.distance;
    }
    public double getDistanceAccuracy() {
        return this.distanceAccuracy;
    }
    public double getVelocity() {
        return this.velocity;
    }
    public double getVelocityAccuracy() {
        return this.velocityAccuracy;
    }
}
