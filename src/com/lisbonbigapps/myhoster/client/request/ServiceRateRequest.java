package com.lisbonbigapps.myhoster.client.request;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.lisbonbigapps.myhoster.client.resources.ServiceResource;
import com.lisbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.lisbonbigapps.myhoster.client.util.ServerHelper;

public class ServiceRateRequest extends SpringAndroidSpiceRequestExtended<ServiceResource> {
    long serviceId;
    int score;

    public ServiceRateRequest(long serviceId, int score) {
	super(ServiceResource.class);

	this.serviceId = serviceId;
	this.score = score;
    }

    @Override
    public ServiceResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/services/" + this.serviceId + "/rate")).buildUpon();
	uriBuilder.appendQueryParameter("score", this.score + "");
	String url = uriBuilder.build().toString();

	RestTemplate restTemplate = this.getRestTemplate();

	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);
	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

	ResponseEntity<ServiceResource> entity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, ServiceResource.class);
	return entity.getBody();
    }
}