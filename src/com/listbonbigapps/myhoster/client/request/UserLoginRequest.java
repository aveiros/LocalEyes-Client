package com.listbonbigapps.myhoster.client.request;

import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.listbonbigapps.myhoster.client.resources.UserResource;
import com.listbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.listbonbigapps.myhoster.client.util.ServerHelper;

public class UserLoginRequest extends SpringAndroidSpiceRequestExtended<UserResource> {
    private String username;
    private String password;

    public UserLoginRequest(String username, String password) {
	super(UserResource.class);
	this.username = username;
	this.password = password;
    }

    @Override
    public UserResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/user/login")).buildUpon();
	uriBuilder.appendQueryParameter("username", this.username);
	uriBuilder.appendQueryParameter("password", this.password);
	String url = uriBuilder.build().toString();

	RestTemplate restTemplate = this.getRestTemplate();
	return restTemplate.getForObject(url, UserResource.class);
    }
}