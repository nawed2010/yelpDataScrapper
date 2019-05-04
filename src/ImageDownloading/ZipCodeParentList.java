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
import java.security.GeneralSecurityException;
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

public class ZipCodeParentList {

	// The url of the website. This is just an example
	private static final String webSiteURL = "https://www.revzilla.com/motorcycle/agv-sportmodular-carbon-solid-helmet";

	// The path of the folder that you want to save the images to
	private static String folderPath2 = "/Users/apple/Documents/ZipCodeProject/";
	private static String fileName = "zip.txt";
	private static String folderPath = "";
	private static String writeFileName = "zip-code-data.txt";
	private static String errorFileName = "error-zip-code-data.txt";
	
	
	static File file = new File(folderPath2 + writeFileName);
	static File errorfile = new File(folderPath2 + writeFileName);
	static BufferedWriter writer=null;
	static BufferedWriter errorwriter=null;
	static {
	try {
		writer= new BufferedWriter(new FileWriter(file, true));
		errorwriter = new BufferedWriter(new FileWriter(errorfile, true));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	

	public static int counter = 1;

	public static void main(String[] args) throws Exception {
		List<String> tmpList = new ArrayList<>();
		List<String> failedList = new ArrayList<>();
		int zipCodeCounter = 0;
		int failedCounter=0;
		int threshold=0;

		try {

			List<String> urlList = readFile();
			String category = "";
			String itemNumber = "";

			int currentProduct = 1;
			int totalProducts = urlList.size();
			
			

			Set<String> dupSet = new TreeSet<>();

			// System.out.println(urlList.size());
			for (String zip : urlList) {
				if(currentProduct!=22) {
					currentProduct++;
					continue;
				}
				threshold++;
				if(threshold==3) {
					Thread.sleep(100000);
					threshold=0;
				}
				
				
				String newZip = StringUtils.leftPad(zip, 5, '0');
				String baseurl = "https://www.yelp.com/search?find_desc=electricians&find_loc=" + newZip;
				int counter = 0;
				String url = "";
				while (true) {
					boolean isDataPresent = false;
					try {

						if(failedCounter==300) {
							throw new GeneralSecurityException("Threshold reached");
						}
						url = baseurl + "&start=" + counter;
						double j=(Math.random()*100)/10;
						long k=((long)j+2)*1000;
						Thread.sleep(k);
						Connection.Response response = Jsoup.connect(url).userAgent("Mozilla").timeout(20000).execute();
						Thread.sleep(2000);
						Document doc = Jsoup.parse(response.body());

						// Get all elements with img tag ,
						// Elements img = doc.getElementsByTag("img");
						Elements metaTags = doc
								.select("div.lemon--div__373c0__1mboc.border-color--default__373c0__2oFDT");
						String href = null;
						// System.out.println("Total Number of Images to be Downloaded : "+img.size());
						for (Element el : metaTags) {
							try {
								
								String name = null;
								String rating = null;
								String review = null;
								String phone = null;
								
								Elements linkTag = el.select(
										"a.lemon--a__373c0__IEZFH.link__373c0__29943.link-color--blue-dark__373c0__1mhJo.link-size--inherit__373c0__2JXk5");
								if (linkTag.size() > 0) {
									Element a = linkTag.get(0);
									href = a.attr("href");
									name = a.text();

									if (href.startsWith("/biz") && !name.startsWith("read more")) {

										if (dupSet.contains(href)) {
											continue;
										} else {
											dupSet.add(href);
											zipCodeCounter++;
											System.out.println("Downloading electrician " + zipCodeCounter);

											if (zipCodeCounter % 10 == 0) {
												System.out.println("sleeping for 20 seconds");
												Thread.sleep(20000);
											}
										}

										Elements ratingTag = el.select(
												"div.lemon--div__373c0__1mboc.i-stars__373c0__30xVZ.border-color--default__373c0__2oFDT.overflow--hidden__373c0__8Jq2I");
										if (ratingTag.size() > 0) {
											rating = ratingTag.get(0).attr("aria-label");
										}

										Elements reviews = el.select(
												"span.lemon--span__373c0__3997G.text__373c0__2pB8f.reviewCount__373c0__2r4xT.text-color--mid__373c0__3G312.text-align--left__373c0__2pnx_");
										if (reviews.size() > 0) {
											review = reviews.get(0).text();
										}
										
										
										Elements phoneDetails = el.select("div.lemon--div__373c0__1mboc.display--inline-block__373c0__2de_K.u-space-b1.border-color--default__373c0__2oFDT");
										if (phoneDetails.size() > 0) {
											phone = phoneDetails.get(0).text();
										//	phone = detail.select("span.biz-phone").get(0).text();

										}

										
										String val = newZip + "#" + name + "#" + href + "#" + rating + "#" + review
												+ "#"  + phone;
										System.out.println(val);
										writer.write(val+"\n");
										writer.flush();
										isDataPresent = true;
									}
								}
							} catch (Exception e) {
								System.out.println("unable to download:" + href);
								failedList.add("electrician#"+href);
								errorwriter.write("electrician#"+href+"\n");
								errorwriter.flush();
								isDataPresent = true;
								failedCounter++;
							}

						}
					} catch(GeneralSecurityException e) {
						
						throw e;
					} catch (Exception e) {

						System.out.println("unable to download:" + url);
						failedList.add("pagination#"+url);
						errorwriter.write("pagination#"+url+"\n");
						errorwriter.flush();
						failedCounter++;
						isDataPresent=true;
					}
					counter = counter + 10;
					if (!isDataPresent)
						break;
				}

				// Connect to the website and get the html

				// System.out.println("Total Number of Images Downloaded : "+img.size());
				System.out.println("Downloaded Data for " + currentProduct + "/" + totalProducts + " Zip Codes");
				currentProduct++;
			}

		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				writeFile(tmpList);
				writeErrorFile(failedList);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
