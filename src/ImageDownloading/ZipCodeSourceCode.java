package ImageDownloading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.Doc;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ZipCodeSourceCode {

	// The url of the website. This is just an example
	private static final String webSiteURL = "https://www.revzilla.com/motorcycle/agv-sportmodular-carbon-solid-helmet";

	// The path of the folder that you want to save the images to
	private static String folderPath2 = "/Users/apple/Documents/ZipCodeProject/";
	private static String fileName = "zip.txt";
	private static String folderPath = "";
	private static String writeFileName = "zip-code-data.txt";
	private static String errorFileName = "error-zip-code-data.txt";

	public static int counter = 1;

	public static void main(String[] args) {
		List<String> tmpList = new ArrayList<>();
		List<String> failedList = new ArrayList<>();
		int zipCodeCounter = 0;
		
		String href = null;
		String name = null;
		String rating = null;
		String review = null;
		String address = null;
		String phone = null;
		String website = null;
		String newZip=null;
		
		File input = new File("/Users/apple/Documents/ZipCodeProject/source.txt");
		try {
			Document doc2 = Jsoup.parse(input, "UTF-8", "http://example.com/");
			Elements htmls = doc2.getElementsByTag("html");
			for(Element doc:htmls) {
			Elements ratingTag = doc.select(
					"div.lemon--div__373c0__1mboc.i-stars__373c0__30xVZ.border-color--default__373c0__2oFDT.overflow--hidden__373c0__8Jq2I");
			if (ratingTag.size() > 0) {
				rating = ratingTag.get(0).attr("aria-label");
			}

			Elements reviews = doc.select(
					"span.lemon--span__373c0__3997G.text__373c0__2pB8f.reviewCount__373c0__2r4xT.text-color--mid__373c0__3G312.text-align--left__373c0__2pnx_");
			if (reviews.size() > 0) {
				review = reviews.get(0).text();
			}
			
			Elements webSiteDetails = doc.getElementsByClass("biz-contact-info_website");
			if (webSiteDetails.size() > 0) {
				Element detail = webSiteDetails.get(0);
				Elements webLink = detail.getElementsByTag("a");
				website = webLink.get(0).text();

			}
			
	

			
			phone = doc.select("span.biz-phone").get(0).text();
			
			website = doc.select("span.biz-website.js-biz-website.js-add-url-tagging").get(0).text();

			

			Elements addressDet = doc.getElementsByTag("address");
			if (addressDet.size() > 0) {
				Element detail = addressDet.get(0);
				address = detail.text();

			}
			
			
			String val = newZip + "#" + name + "#" + href + "#" + rating + "#" + review
					+ "#" + address + "#" + website + "#" + phone;
			System.out.println(val);
			tmpList.add(val);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

	public static void downloadImages(String search, String path) throws IOException {

		path = path + "image" + counter + ".jpg";
		// System.out.println("Downloading Image #"+counter);
		counter++;
		// This will get input data from the server
		InputStream inputStream = null;

		// This will read the data from the server;
		OutputStream outputStream = null;

		try {
			// This will open a socket from client to server
			URL url = new URL(search);

			// This user agent is for if the server wants real humans to visit
			String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

			// This socket type will allow to set user_agent
			URLConnection con = url.openConnection();

			// Setting the user agent
			con.setRequestProperty("User-Agent", USER_AGENT);

			// Getting content Length
			int contentLength = con.getContentLength();
			// System.out.println("File contentLength = " + contentLength + " bytes");

			// Requesting input data from server
			inputStream = con.getInputStream();

			// System.out.println(path);
			// Open local file writer
			outputStream = new FileOutputStream(path);

			// Limiting byte written to file per loop
			byte[] buffer = new byte[2048];

			// Increments file size
			int length;
			int downloaded = 0;

			// Looping until server finishes
			while ((length = inputStream.read(buffer)) != -1) {
				// Writing data
				outputStream.write(buffer, 0, length);
				downloaded += length;
				// System.out.println("Downlad Status: " + (downloaded * 100) / (contentLength *
				// 1.0) + "%");

			}
		} catch (Exception ex) {
			// Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
		}

		// closing used resources
		// The computer will not be able to use the image
		// This is a must
		outputStream.close();
		inputStream.close();
	}

	public static List<String> readFile() throws IOException {

		File file = new File(folderPath2 + fileName);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<String> tmpList = new ArrayList<>();
		while (true) {
			String tmpString = reader.readLine();
			if (tmpString == null || tmpString.isEmpty()) {
				break;
			}
			// System.out.println(tmpString);
			tmpList.add(tmpString);
		}
		return tmpList;

	}

	public static void createFolder(String folderPath) {
		File theDir2 = new File(folderPath);

		if (!theDir2.exists()) {
			// System.out.println("creating directory: " + theDir2.getName());
			boolean result = false;

			try {
				theDir2.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				// System.out.println("DIR created");
			}
		}
	}

	public static void writeFile(List<String> tmpList) throws IOException {

		File file = new File(folderPath2 + writeFileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

		for (String rec : tmpList) {
			writer.write(rec + "\n");
		}
		writer.close();

	}
	public static void writeErrorFile(List<String> tmpList) throws IOException {

		File file = new File(folderPath2 + errorFileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

		for (String rec : tmpList) {
			writer.write(rec + "\n");
		}
		writer.close();

	}

}
