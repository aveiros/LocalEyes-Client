package com.lisbonbigapps.myhoster.client.request;

import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.lisbonbigapps.myhoster.client.resources.ListServiceResource;
import com.lisbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.lisbonbigapps.myhoster.client.util.ServerHelper;

public class ServicesRequest extends SpringAndroidSpiceRequestExtended<ListServiceResource> {
    public ServicesRequest() {
	super(ListServiceResource.class);
    }

    @Override
    public ListServiceResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/services")).buildUpon();
	String url = uriBuilder.build().toString();

	RestTemplate restTemplate = this.getRestTemplate();
	return restTemplate.getForObject(url, ListServiceResource.class);
    }
}