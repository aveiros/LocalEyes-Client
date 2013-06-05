package com.listbonbigapps.myhoster.client.spring;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.listbonbigapps.myhoster.client.util.HttpClientSingleton;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public abstract class SpringAndroidSpiceRequestExtended<T> extends SpringAndroidSpiceRequest<T> {
    public SpringAndroidSpiceRequestExtended(Class<T> _class) {
	super(_class);
    }

    @Override
    public RestTemplate getRestTemplate() {
	RestTemplate restTemplate = super.getRestTemplate();
	HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientSingleton.getInstance());
	restTemplate.setRequestFactory(requestFactory);
	return restTemplate;
    }
}
