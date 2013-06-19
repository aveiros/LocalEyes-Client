package com.lisbonbigapps.myhoster.client.resources;

import java.util.List;

public class UserResource extends RootResource {
    long id;
    String username;
    String name;
    String photo;
    String phoneNumber;
    List<String> interests;

    LocationResource location;

    ServiceProfileResource service;

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

    public List<String> getInterests() {
	return interests;
    }

    public void setInterests(List<String> interests) {
	this.interests = interests;
    }

    public ServiceProfileResource getService() {
	return service;
    }

    public void setService(ServiceProfileResource service) {
	this.service = service;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }
}
