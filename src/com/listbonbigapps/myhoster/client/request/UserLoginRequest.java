package com.listbonbigapps.myhoster.client.request;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.listbonbigapps.myhoster.client.resources.UserResource;
import com.listbonbigapps.myhoster.client.util.HttpClientSingleton;
import com.listbonbigapps.myhoster.client.util.ServerHelper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class UserLoginRequest extends SpringAndroidSpiceRequest<UserResource> {
	private String username;
	private String password;

	public UserLoginRequest(String username, String password) {
		super(UserResource.class);
		this.username = username;
		this.password = password;
	}

	@Override
	public UserResource loadDataFromNetwork() throws Exception {
		Uri.Builder uriBuilder = Uri.parse(
				ServerHelper.buildRestUrl("/user/login")).buildUpon();
		uriBuilder.appendQueryParameter("username", this.username);
		uriBuilder.appendQueryParameter("password", this.password);
		String url = uriBuilder.build().toString();

		RestTemplate restTemplate = this.getRestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				HttpClientSingleton.getInstance());
		restTemplate.setRequestFactory(requestFactory);

		return restTemplate.getForObject(url, UserResource.class);
	}
}