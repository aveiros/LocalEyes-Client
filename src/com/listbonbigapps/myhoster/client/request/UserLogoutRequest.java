package com.listbonbigapps.myhoster.client.request;

import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.listbonbigapps.myhoster.client.resources.MessageResource;
import com.listbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.listbonbigapps.myhoster.client.util.ServerHelper;

public class UserLogoutRequest extends SpringAndroidSpiceRequestExtended<MessageResource> {
    public UserLogoutRequest() {
	super(MessageResource.class);
    }

    @Override
    public MessageResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/user/logout")).buildUpon();
	String url = uriBuilder.build().toString();

	RestTemplate restTemplate = this.getRestTemplate();
	return restTemplate.getForObject(url, MessageResource.class);
    }
}