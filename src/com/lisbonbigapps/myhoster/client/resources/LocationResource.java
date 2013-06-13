package com.lisbonbigapps.myhoster.client.resources;

public class LocationResource extends RootResource {
    double latitude;
    double longitude;

    public double getLatitude() {
	return latitude;
    }

    public void setLatitude(double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(double longitude) {
	this.longitude = longitude;
    }
}
