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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ZipCodeDownloader {

    //The url of the website. This is just an example
    private static final String webSiteURL = "https://www.revzilla.com/motorcycle/agv-sportmodular-carbon-solid-helmet";

    //The path of the folder that you want to save the images to
    private static  String folderPath2 = "/Users/apple/Documents/TheHomeDepot/";
     private static String fileName="zip.txt";
     private static String writeFileName="5-mile.txt";
    
    public static int counter=1;

    public static void main(String[] args) {

        try {
        	List<String> zipCodeList=readFile();
        	List<String> writeList=new ArrayList<>();
        	String miles="10";
        	writeList.add("parentzipcode#zip#city#county#state#distance");
        	int counter=1;
        	
        for(String zip:zipCodeList) {
        	try {
           //Connect to the website and get the html
        	
        	String zipWithLen5=StringUtils.leftPad(zip,5,'0');
        	String url="https://www.zip-codes.com/zip-code-radius-finder.asp?zipmileslow="+miles+"&zipmileshigh="+miles+"&zip1="+zipWithLen5+"&submit=Search";
            Document doc = Jsoup.connect(url).data("query", "Java")
            		  .userAgent("Mozilla")
            		  .cookie("auth", "token")
            		  .timeout(20000)
            		  .post();

            //Get all elements with img tag ,
            //  Elements img = doc.getElementsByTag("img");
            String var=zip+"#";
            Element table=doc.getElementsByTag("table").get(2);
            Elements rows = table.select("tr");
            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

                String newVal=var+cols.get(1).text()+"#"+cols.get(2).text()+"#"+cols.get(3).text()+"#"+cols.get(4).text()+"#"+cols.get(6).text();
                writeList.add(newVal);
                
            }
            
            
            System.out.println("Downloaded "+counter+"/"+zipCodeList.size());
            counter++;
        	}catch(Exception e) {
        		System.out.println("unable to download "+counter);
        		counter++;
        	}
            
            
        }
        
        writeFile(writeList);
            
          
           

        } catch (Exception ex) {
            System.err.println("There was an error");
            Logger.getLogger(ZipCodeDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    public static void downloadImages(String search, String path) throws IOException {

    	path=path+"image"+counter+".jpg";
    //	System.out.println("Downloading Image #"+counter);
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

            //Getting content Length
            int contentLength = con.getContentLength();
            //System.out.println("File contentLength = " + contentLength + " bytes");


            // Requesting input data from server
            inputStream = con.getInputStream();

            //System.out.println(path);
            // Open local file writer
            outputStream = new FileOutputStream(path);

            // Limiting byte written to file per loop
            byte[] buffer = new byte[2048];

            // Increments file size
            int length;
            int downloaded = 0; 

            // Looping until server finishes
            while ((length = inputStream.read(buffer)) != -1) 
            {
                // Writing data
                outputStream.write(buffer, 0, length);
                downloaded+=length;
                //System.out.println("Downlad Status: " + (downloaded * 100) / (contentLength * 1.0) + "%");


            }
        } catch (Exception ex) {
            //Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        // closing used resources
        // The computer will not be able to use the image
        // This is a must
        outputStream.close();
        inputStream.close();
    }
    
    public static List<String> readFile() throws IOException{
    	
    	File file=new File(folderPath2+fileName);
    	BufferedReader reader=new BufferedReader(new FileReader(file));
    	List<String> tmpList=new ArrayList<>();
    	while(true) {
    		String tmpString=reader.readLine();
    		if(tmpString==null||tmpString.isEmpty()) {
    			break;
    		}
    		//System.out.println(tmpString);
    		tmpList.add(tmpString);
    	}
		return tmpList;
    	
    }
    
    public static void createFolder(String folderPath) {
    	File theDir2 = new File(folderPath);
        
        
        if (!theDir2.exists()) {
         //   System.out.println("creating directory: " + theDir2.getName());
            boolean result = false;

            try{
                theDir2.mkdir();
                result = true;
            } 
            catch(SecurityException se){
                //handle it
            }        
            if(result) {    
          //      System.out.println("DIR created");  
            }
        }
        
        
    }
    
    
public static void writeFile(List<String> tmpList) throws IOException{
    	
    	File file=new File(folderPath2+writeFileName);
    	BufferedWriter writer=new BufferedWriter(new FileWriter(file));
    	
    	for(String rec:tmpList) {
    		writer.write(rec+"\n");
    	}
    	writer.close();
    	
		
    	
    }

}
