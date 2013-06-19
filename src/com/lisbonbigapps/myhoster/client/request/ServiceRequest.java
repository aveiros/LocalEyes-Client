package com.lisbonbigapps.myhoster.client.request;

import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.lisbonbigapps.myhoster.client.resources.ServiceResource;
import com.lisbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.lisbonbigapps.myhoster.client.util.ServerHelper;

public class ServiceRequest extends SpringAndroidSpiceRequestExtended<ServiceResource> {
    long serviceId;

    public ServiceRequest(long serviceId) {
	super(ServiceResource.class);
	this.serviceId = serviceId;
    }

    @Override
    public ServiceResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/services/" + this.serviceId)).buildUpon();
	String url = uriBuilder.build().toString();

	RestTemplate restTemplate = this.getRestTemplate();
	return restTemplate.getForObject(url, ServiceResource.class);
    }
}