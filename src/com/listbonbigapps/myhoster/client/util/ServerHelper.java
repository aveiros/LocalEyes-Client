package com.listbonbigapps.myhoster.client.util;

public class ServerHelper {
	private static String ServerIp = "192.168.1.67";
	private static String ServerPort = "8080";
	private static String ServerRestUrl = "http://" + ServerIp + ":"
			+ ServerPort + "/myHoster/rest";

	public static String getServerIp() {
		return ServerIp;
	}

	public static String getServerPort() {
		return ServerPort;
	}

	public static String buildRestUrl(String endPoint) {
		return ServerRestUrl + endPoint;
	}
}
