package com.lisbonbigapps.myhoster.client.model;

//A very simple model that defines the HOSTERS  data that this app deals with.
public class HosterModel {
    public long id;
    public String address;
    public String name;
    public int distance;
    public int picture;
    public int rating;
    public int interests;

    public HosterModel(long id, String name, String address, int picture, int distance, int interests, int rating) {
	this.id = id;
	this.name = name;
	this.address = address;
	this.picture = picture;
	this.distance = distance;
	this.interests = interests;
	this.rating = rating;
    }
}
