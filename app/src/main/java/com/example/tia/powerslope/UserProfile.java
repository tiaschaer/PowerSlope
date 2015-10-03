package com.example.tia.powerslope;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tia on 9/27/15.
 */
public class UserProfile {

    private String name;
    private double Mbike;
    private double MriderInitial;
    private double Cr;
    private double Rho;
    private double Ad;

    public void fromJson(JSONObject userProfileJson) {
        try {
            this.name = userProfileJson.getString("name");
            this.Mbike = userProfileJson.getDouble("Mbike");
            this.MriderInitial = userProfileJson.getDouble("MriderInitial");
            this.Cr = userProfileJson.getDouble("Cr");
            this.Rho = userProfileJson.getDouble("Rho");
            this.Ad = userProfileJson.getDouble("Ad");
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject toJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", this.getName());
            jsonObject.put("Mbike", this.getMbike());
            jsonObject.put("MriderInitial", this.getMriderInitial());
            jsonObject.put("Cr", this.getCr());
            jsonObject.put("Rho", this.getRho());
            jsonObject.put("Ad", this.getAd());
            return jsonObject;
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void createTestUser(String userName) {
        this.name = userName;
        this.Mbike = 8;
        this.MriderInitial = 78;
        this.Cr = 0.0048;
        this.Ad = 0.3633;
        this.Rho = 1;
    }

    public String getName() {
        return this.name;
    }
    public double getMbike() {
        return this.Mbike;
    }
    public double getMriderInitial() {
        return this.MriderInitial;
    }
    public double getCr() {
        return this.Cr;
    }
    public double getRho() {
        return this.Rho;
    }
    public double getAd() {
        return this.Ad;
    }

}
