package com.listbonbigapps.myhoster.client.request;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.listbonbigapps.myhoster.client.resources.MessageResource;
import com.listbonbigapps.myhoster.client.util.HttpClientSingleton;
import com.listbonbigapps.myhoster.client.util.ServerHelper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class UserLogoutRequest extends SpringAndroidSpiceRequest<MessageResource> {
	public UserLogoutRequest() {
		super(MessageResource.class);
	}

	@Override
	public MessageResource loadDataFromNetwork() throws Exception {
		Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/user/logout"))
				.buildUpon();
		String url = uriBuilder.build().toString();

		RestTemplate restTemplate = this.getRestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				HttpClientSingleton.getInstance());
		restTemplate.setRequestFactory(requestFactory);

		return restTemplate.getForObject(url, MessageResource.class);
	}
}