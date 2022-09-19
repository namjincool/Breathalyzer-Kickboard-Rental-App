package org.techtown.capston;

import java.util.HashMap;

public class lat_lon {

    public double latitude;
    public double longitude;
    public HashMap<String,Double> map;
    public lat_lon(){

    }
    public lat_lon(double latitude,double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
