package com.lisbonbigapps.myhoster.client.util;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientSingleton {
    private static HttpClient INSTANCE;

    private HttpClientSingleton() {
    }

    public static HttpClient getInstance() {
	if (INSTANCE == null) {
	    HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(params, 5000);
	    HttpConnectionParams.setSoTimeout(params, 5000);
	    INSTANCE = new DefaultHttpClient(params);
	}

	return INSTANCE;
    }
}