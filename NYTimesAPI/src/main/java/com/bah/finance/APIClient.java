package com.bah.finance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
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

@SuppressWarnings("deprecation")
public class APIClient {

	public static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static final String baseURL = "http://api.nytimes.com/svc/search/v2/articlesearch";
	public static final String extension = ".json";
	public static String key_param = "api-key=";
	public static final String queryParam = "q=";
	public static final String startDateParam = "begin_date=";
	public static final String endDateParam = "end_date=";
	public static final String pageParam = "page=";

	public DefaultHttpClient httpClient;

	private static int apiCalls;
	// private static final int MAX_CALLS_PER_DAY = 10000;
	private static final int MAX_CALLS_PER_DAY = 9500;
	private static final int MAX_ERRORS = 200;
	private int errors = 0;
	
	private static final long CALL_DELAY = 100;

	private static final String fileSep = System.getProperty("file.separator");

	public static void main(String args[]) throws ClientProtocolException, IOException, ParseException,
			InterruptedException {

		String startDate = null, endDate = null, key, outputDirectory;

		if (args.length < 5) {
			System.out.println("Please use 5 arguments: startdate (yyyyMMdd), endDate (yyyyMMdd),"
					+ " apiKey (String), outputDirectory, and downloadArticles (boolean)");
			throw new RuntimeException();
		}

		if (dateFormat.parse(args[0]) != null) {
			startDate = args[0];
		}

		if (dateFormat.parse(args[1]) != null) {
			endDate = args[1];
		}

		key = args[2];

		outputDirectory = args[3];
		
		boolean downloadArticles = Boolean.parseBoolean(args[4]);

		APIClient apiClient = new APIClient();

		apiClient.query(startDate, endDate, key, outputDirectory, downloadArticles);

	}

	public Set<String> query(String startDate, String endDate, String apiKey, String baseOutputDirectory,
			boolean downloadArticles) throws ClientProtocolException, IOException, InterruptedException {
		apiCalls = 0;
		errors = 0;
		
		// String[] queryTerms = ("crude oil gasoline gas pump fuel exxon "
		// + "chevron texaco shell bp lukos statoil transocean "
		// + "offshore drilling spill petroleum ship refinery "
		// + "diesel light sweet brent hurricane tropical storm")
		String[] queryTerms = "(crude oil)".split(" ");
		StringBuilder query = new StringBuilder();

		for (String queryTerm : queryTerms) {
			if (query.length() != 0) {
				query.append(" ");
			}
			query.append(queryTerm);
		}

		System.out.println("query: " + query.toString());

		String encodedQuery = "fq=body:" + URLEncoder.encode("((" + query.toString() + ")") + "%20OR%20" + "subject:"
				+ URLEncoder.encode("(" + query.toString() + "))") + "%20AND%20" + "source:"
				+ URLEncoder.encode("(\"The New York Times\")");

		Set<String> urls = getArticleURLs(encodedQuery, startDate, endDate, apiKey, baseOutputDirectory, downloadArticles);

		System.out.println("Retrieved " + urls.size() + " urls");
		
		return urls;
	}

	public Set<String> getArticleURLs(String query, String startDate, String endDate, String apiKey,
			String baseOutputDirectory, boolean downloadArticles) throws ClientProtocolException, IOException, InterruptedException {

		Set<String> urls = new TreeSet<String>();

		String outputDirectory = null;
		OutputStream urlOutput = null;

		if (baseOutputDirectory != null) {
			outputDirectory = baseOutputDirectory + fileSep + new Date().getTime() + fileSep + startDate + "_"
					+ endDate + fileSep;
			System.out.println("outputDirectory= " + outputDirectory);
			File dir = new File(outputDirectory);
			dir.mkdirs();

			urlOutput = new FileOutputStream(outputDirectory + "urls.txt");
		}

		String url = baseURL + extension + "?";

		url += key_param + apiKey;
		url += "&" + query;
		url += "&" + startDateParam + startDate;
		url += "&" + endDateParam + endDate;

		System.out.println("url: " + url);

		httpClient = new DefaultHttpClient();
		
		String jsonString = null;
		int attempts = 0;
		while(jsonString == null && attempts < 10){
			try{
				jsonString = getPage(url, 0);
			} catch (RuntimeException e){
				errors++;
				attempts++;
				if(errors > MAX_ERRORS){
					throw new RuntimeException("Too many errors");
				}
				System.out.println("Sleeping for 20 seconds");
				Thread.sleep(20000);
			}
		}

		Object obj = JSONValue.parse(jsonString.toString());
		JSONObject finalResult = (JSONObject) obj;

		JSONObject jsonResponse = (JSONObject) finalResult.get("response");
		JSONObject meta = (JSONObject) jsonResponse.get("meta");
		Long hits = (Long) meta.get("hits");
		System.out.println("hits = " + hits);
		long pages = hits / 10 + 1;
		System.out.println("pages = " + pages);

		Set<String> firstPageURLs = getURLs(jsonString);

		if (urlOutput != null) {
			for (String pageURL : firstPageURLs) {
				IOUtils.write(pageURL + "\n", urlOutput);
			}
			if(downloadArticles){
				downloadArticles(urls, outputDirectory);
			}
		}

		urls.addAll(firstPageURLs);

		// uncomment to get all URLs (without just returns first page)
		if (pages > 1) {
			for (int i = 1; i < pages; i++) {
//				if(i % 5 == 0){
//					httpClient.getConnectionManager().shutdown();
//					httpClient = new DefaultHttpClient();
//				}
				Thread.sleep(CALL_DELAY);
				System.out.println("Querying for page " + i);
				try{
					String page = getPage(url, i);
					Set<String> pageURLs = getURLs(page);
					if (urlOutput != null) {
						for (String pageURL : pageURLs) {
							IOUtils.write(pageURL + "\n", urlOutput);
						}
						if(downloadArticles){
							downloadArticles(urls, outputDirectory);
						}
					}
					urls.addAll(pageURLs);
				} catch (RuntimeException e){
					errors++;
					if(errors > MAX_ERRORS){
						throw new RuntimeException("Too many errors");
					}
					i--;
					System.out.println("Sleeping for 20 seconds");
					Thread.sleep(20000);
				}
			}
		}

		if (urlOutput != null) {
			IOUtils.closeQuietly(urlOutput);
		}
		httpClient.getConnectionManager().shutdown();

		return urls;
	}

	public String getPage(String url, int page) throws ClientProtocolException, IOException {

		//httpClient = new DefaultHttpClient();
		if (++apiCalls > MAX_CALLS_PER_DAY) {
			throw new RuntimeException("Max calls reached. Page = " + page);
		}

		String queryURL = url + "&" + pageParam + page;

		HttpGet get = new HttpGet(queryURL);

		HttpResponse response = httpClient.execute(get);

		InputStream content = response.getEntity().getContent();
		if (response.getStatusLine().getStatusCode() >= 300) {
			throw new RuntimeException("Status code = " + response.getStatusLine().getStatusCode() + "\n"
					+ IOUtils.toString(content) + "\n" + "page = " + page);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(content));

		String output;
		StringBuilder jsonString = new StringBuilder();
		while ((output = br.readLine()) != null) {
			jsonString.append(output);
		}
		IOUtils.closeQuietly(content);
		get.releaseConnection();
		//httpClient.getConnectionManager().shutdown();
		return jsonString.toString();
	}

	public Set<String> getURLs(String jsonString) {
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

	public void downloadArticles(Set<String> urls, String outputDirectory) throws IOException, InterruptedException {

		for (String url : urls) {
			try {
				Thread.sleep(CALL_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			String article;
			
			try{
				article = downloadArticle(url);
			} catch (RuntimeException e){
				errors++;
				if(errors > MAX_ERRORS){
					throw new RuntimeException("Too many errors");
				}
				System.out.println("Sleeping for 20 seconds");
				Thread.sleep(20000);
				article = downloadArticle(url);
			}
			String articleName = url.substring(0, url.length() - 2);
			articleName = articleName.substring(articleName.lastIndexOf("/") + 1).replaceAll("[?]", "_");
			OutputStream out = new FileOutputStream(outputDirectory + articleName + ".html");
			IOUtils.write(article, out);
			IOUtils.closeQuietly(out);
		}
	}

	public String downloadArticle(String url) throws ClientProtocolException, IOException {

		if (++apiCalls > MAX_CALLS_PER_DAY) {
			throw new RuntimeException("Max calls reached");
		}

		HttpGet get = new HttpGet(url);

		HttpResponse response = httpClient.execute(get);

		InputStream content = response.getEntity().getContent();
		if (response.getStatusLine().getStatusCode() >= 400) {
			throw new RuntimeException("Status code = " + response.getStatusLine().getStatusCode() + "\n"
					+ IOUtils.toString(content));
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(content));

		String output;
		StringBuilder htmlString = new StringBuilder();
		while ((output = br.readLine()) != null) {
			htmlString.append(output);
		}
		IOUtils.closeQuietly(content);
		return htmlString.toString();
	}
}
