package com.lisbonbigapps.myhoster.client.resources;

public class UserResource extends RootResource {
    long id;
    String username;
    String name;
    String photo;
    LocationResource location;

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public LocationResource getLocation() {
	return location;
    }

    public void setLocation(LocationResource location) {
	this.location = location;
    }

    public String getPhoto() {
	return photo;
    }

    public void setPhoto(String photo) {
	this.photo = photo;
    }
}
