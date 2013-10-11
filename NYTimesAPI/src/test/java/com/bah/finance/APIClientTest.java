package com.bah.finance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

public class APIClientTest {

	public static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
	
	//@Test
	public void testQuery() throws ClientProtocolException, IOException, InterruptedException{
		
		int startingYear = 2010;
		
		for(int y = startingYear; y <= 2013; y++){
			//String startDate = "19930101";
			//String endDate = "20131010";
			//String endDate = "19930131";
			String apiKey1 = "276ab1a2db04cce7f878671cf9f2f2c2:8:68233964";
			String apiKey2 = "6251375db21329deb5245573d9b0ea77:1:68230793";
			String outputDir = "C:\\articles\\" + dateFormat.format(new Date());
			String year = "" + y;
			boolean downloadArticles = false;
			
			APIClient client = new APIClient();
			
			// There's a slicker way of doing this using the Calendar object
			// but at the moment I don't care. It would take longer to write that
			// than just doing this
			String months[] = "0101 0115 0116 0131 0201 0215 0216 0228 0301 0315 0316 0331 0401 0415 0416 0430 0501 0515 0516 0531 0601 0615 0616 0630 0701 0715 0716 0731 0801 0815 0816 0831 0901 0915 0916 0930 1001 1015 1016 1031 1101 1115 1116 1130 1201 1215 1216 1231".split(" ");
			//String months[] = "1101 1130 1201 1231".split(" ");
			Set<String> urls = new TreeSet<String>();
			for(int i = 0; i < months.length; i = i + 2){
				String startDate = year + months[i];
				String endDate = year + months[i + 1];
				System.out.println("querying for: " + startDate + "_" + endDate);
				String apiKey;
				if(i % 4 == 0){
					apiKey = apiKey1;
				}
				else{
					apiKey = apiKey2;
				}
				urls.addAll(client.query(startDate, endDate, apiKey, outputDir, downloadArticles));
			}
			
			OutputStream out = new FileOutputStream(outputDir + "_" + year + ".txt");
			for(String url : urls){
				IOUtils.write(url + "\n", out);
			}
			IOUtils.closeQuietly(out);
		}
	}
	
	@Test
	public void testFormat() {
		System.out.println(String.format("Try%s it twice: %s", "2013", "2013"));
	}
}
