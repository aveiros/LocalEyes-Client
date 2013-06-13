package com.lisbonbigapps.myhoster.client.request;

import org.springframework.web.client.RestTemplate;

import android.net.Uri;

import com.lisbonbigapps.myhoster.client.resources.ListUserResource;
import com.lisbonbigapps.myhoster.client.spring.SpringAndroidSpiceRequestExtended;
import com.lisbonbigapps.myhoster.client.util.ServerHelper;

public class HostsAroundRequest extends SpringAndroidSpiceRequestExtended<ListUserResource> {
    private Double latitude;
    private Double longitude;
    private double distance;

    public HostsAroundRequest(double distance, Double latitude, Double longitude) {
	super(ListUserResource.class);
	this.latitude = latitude;
	this.longitude = longitude;
	this.distance = distance;
    }

    @Override
    public ListUserResource loadDataFromNetwork() throws Exception {
	Uri.Builder uriBuilder = Uri.parse(ServerHelper.buildRestUrl("/hosts/find")).buildUpon();
	uriBuilder.appendQueryParameter("distance", String.format("%f", this.distance));

	if (this.latitude != null && this.longitude != null) {
	    uriBuilder.appendQueryParameter("latitude", this.latitude + "");
	    uriBuilder.appendQueryParameter("longitude", this.longitude + "");
	}

	String url = uriBuilder.build().toString();

	RestTemplate restTemplate = this.getRestTemplate();
	return restTemplate.getForObject(url, ListUserResource.class);
    }
}
