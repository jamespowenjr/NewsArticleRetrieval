package com.bah.finance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class APIClient {

	public static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	public static final String baseURL = "http://api.nytimes.com/svc/search/v2/articlesearch";
	public static final String extension = ".json";
	public static final String key = "api-key=6251375db21329deb5245573d9b0ea77:1:68230793";
	public static final String query = "q=";
	public static final String source = "fq=source:" + URLEncoder.encode("(\"The New York Times\")");
	public static final String startDate = "begin_date=20130101";
	public static final String endDate = "end_date=" + dateFormat.format(new Date());
	public static final String pageParam = "page=";

	public static void main(String args[]) throws ClientProtocolException,
			IOException {

		String url = baseURL + extension + "?";

		url += key;
		url += "&" + source;
		url += "&" + query + URLEncoder.encode("\"crude oil\"");
		url += "&" + startDate;
		url += "&" + endDate;
		url += "&" + pageParam + "0";
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		System.out.println(url);
		HttpGet get = new HttpGet(url);

		HttpResponse response = httpClient.execute(get);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		httpClient.getConnectionManager().shutdown();
	}

}
