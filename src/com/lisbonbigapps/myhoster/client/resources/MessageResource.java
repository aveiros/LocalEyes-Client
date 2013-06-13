package com.lisbonbigapps.myhoster.client.resources;

public class MessageResource extends RootResource {
    private String message;

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }
}
