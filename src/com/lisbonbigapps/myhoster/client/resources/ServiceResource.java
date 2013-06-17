package com.lisbonbigapps.myhoster.client.resources;

public class ServiceResource extends RootResource {
    long id;
    String status;
    UserResource hoster;
    UserResource traveller;

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public UserResource getHoster() {
	return hoster;
    }

    public void setHoster(UserResource hoster) {
	this.hoster = hoster;
    }

    public UserResource getTraveller() {
	return traveller;
    }

    public void setTraveller(UserResource traveller) {
	this.traveller = traveller;
    }
}