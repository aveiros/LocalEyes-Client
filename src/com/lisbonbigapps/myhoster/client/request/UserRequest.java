package com.lisbonbigapps.myhoster.client.request;

import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.lisbonbigapps.myhoster.client.resources.UserResource;
import com.lisbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.lisbonbigapps.myhoster.client.util.ServerHelper;

public class UserRequest extends SpringAndroidSpiceRequestExtended<UserResource> {
    private long id;

    public UserRequest(long id) {
	super(UserResource.class);
	this.id = id;
    }

    @Override
    public UserResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/user/" + this.id)).buildUpon();
	String url = uriBuilder.toString();
	RestTemplate restTemplate = this.getRestTemplate();
	return restTemplate.getForObject(url, UserResource.class);
    }
}