package com.listbonbigapps.myhoster.client.util;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientSingleton {

	private static final HttpClient INSTANCE = new DefaultHttpClient();

	private HttpClientSingleton() {
	}

	public static HttpClient getInstance() {
		return INSTANCE;
	}

}
