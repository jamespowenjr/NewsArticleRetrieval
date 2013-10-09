package com.bah.finance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class APIClient {

	public static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static final String baseURL = "http://api.nytimes.com/svc/search/v2/articlesearch";
	public static final String extension = ".json";
	public static final String key = "api-key=6251375db21329deb5245573d9b0ea77:1:68230793";
	public static final String queryParam = "q=";
	public static final String source = "fq=source:"
			+ URLEncoder.encode("(\"The New York Times\")");
	public static final String startDate = "begin_date=20130101";
	public static final String endDate = "end_date="
			+ dateFormat.format(new Date());
	public static final String pageParam = "page=";

	public static DefaultHttpClient httpClient = new DefaultHttpClient();

	private static String articleDirectory = "C:\\articles\\";

	public static void main(String args[]) throws ClientProtocolException,
			IOException {
		String[] queryTerms = "crude oil gasoline gas pump fuel exxon chevron texaco shell bp lukos statoil transocean offshore drilling spill petroleum ship refinery diesel light sweet brent hurricane tropical storm".split(" ");
		StringBuilder query = new StringBuilder();

		for(String queryTerm : queryTerms){
			if(query.length() != 0){
				query.append("&");
			}
			query.append("q=\"" + queryTerm + "\"");
		}
		System.out.println("query: " + query.toString());
		Set<String> urls = getArticleURLs(query.toString());

		for (String url : urls) {
			System.out.println(url);
		}
		
		//downloadArticles(urls);

		httpClient.getConnectionManager().shutdown();

	}

	public static Set<String> getArticleURLs(String query)
			throws ClientProtocolException, IOException {
		Set<String> urls = new TreeSet<String>();

		String url = baseURL + extension + "?";

		url += key;
		url += "&" + source;
		url += "&" + queryParam + URLEncoder.encode(query);
		url += "&" + startDate;
		url += "&" + endDate;

		String jsonString = getPage(url, 0);

		Object obj = JSONValue.parse(jsonString.toString());
		JSONObject finalResult = (JSONObject) obj;

		JSONObject jsonResponse = (JSONObject) finalResult.get("response");
		JSONObject meta = (JSONObject) jsonResponse.get("meta");
		Long hits = (Long) meta.get("hits");
		System.out.println(hits + " = " + hits);
		long pages = hits / 10 + 1;
		System.out.println("pages = " + pages);
		JSONArray docs = (JSONArray) jsonResponse.get("docs");
		for (Object doc : docs) {
			String webURL = (String) ((JSONObject) doc).get("web_url");
			urls.add(webURL);
		}

		if (pages > 1) {
			for (int i = 1; i < pages; i++) {
				String page = getPage(url, i);
				urls.addAll(getURLs(page));
			}
		}

		return urls;
	}

	public static String getPage(String url, int page)
			throws ClientProtocolException, IOException {
		String queryURL = url + "&" + pageParam + page;

		HttpGet get = new HttpGet(queryURL);

		HttpResponse response = httpClient.execute(get);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));

		String output;
		StringBuilder jsonString = new StringBuilder();
		while ((output = br.readLine()) != null) {
			jsonString.append(output);
		}

		return jsonString.toString();
	}

	public static Set<String> getURLs(String jsonString) {
		Set<String> urls = new TreeSet<String>();

		Object obj = JSONValue.parse(jsonString.toString());
		JSONObject finalResult = (JSONObject) obj;

		JSONObject jsonResponse = (JSONObject) finalResult.get("response");

		JSONArray docs = (JSONArray) jsonResponse.get("docs");
		for (Object doc : docs) {
			String webURL = (String) ((JSONObject) doc).get("web_url");
			urls.add(webURL);
		}

		return urls;
	}

	public static void downloadArticles(Set<String> urls) throws IOException {
		
		Date currentTime = new Date();
		
		File dir = new File(articleDirectory + currentTime.getTime());
		dir.mkdir();
		
		for (String url : urls) {
			String article = downloadArticle(url);
			String articleName = url.substring(0, url.length() - 2);
			articleName = articleName.substring(articleName.lastIndexOf("/") + 1).replaceAll("[?]", "_");
			OutputStream out = new FileOutputStream(articleDirectory + currentTime.getTime() + "\\" + articleName + ".html");
			IOUtils.write(article, out);
		}
	}

	public static String downloadArticle(String url) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);

		HttpResponse response = httpClient.execute(get);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));

		String output;
		StringBuilder htmlString = new StringBuilder();
		while ((output = br.readLine()) != null) {
			htmlString.append(output);
		}

		return htmlString.toString();
	}
}
