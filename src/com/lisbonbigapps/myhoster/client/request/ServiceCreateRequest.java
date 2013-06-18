package com.lisbonbigapps.myhoster.client.request;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.lisbonbigapps.myhoster.client.resources.ServiceResource;
import com.lisbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.lisbonbigapps.myhoster.client.util.ServerHelper;

public class ServiceCreateRequest extends SpringAndroidSpiceRequestExtended<ServiceResource> {
    long hostId;

    public ServiceCreateRequest(long hostId) {
	super(ServiceResource.class);
	this.hostId = hostId;
    }

    @Override
    public ServiceResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/services")).buildUpon();
	uriBuilder.appendQueryParameter("host", this.hostId + "");
	String url = uriBuilder.build().toString();

	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);
	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

	RestTemplate restTemplate = this.getRestTemplate();
	return restTemplate.postForObject(url, httpEntity, ServiceResource.class);
    }
}