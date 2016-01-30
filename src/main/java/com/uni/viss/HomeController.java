package com.uni.viss;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
 
import java.text.DateFormat;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.commons.codec.binary.Base64;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_KeywordParams;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController implements ServletContextAware{
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private ServletContext servletContext;
	ArrayList<BookInfo> finalBookList;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);		
		String formattedDate = dateFormat.format(date);		
		model.addAttribute("serverTime", formattedDate );	
		return "home";
	}
	
	@RequestMapping(value = "getBooks", method = RequestMethod.POST)
	public ModelAndView getBooks(Model model, @RequestParam("fileName")MultipartFile data){
		
	     ResponseDataInJson responseObject = new ResponseDataInJson();
		 if (!data.isEmpty()) {
	            try {
	                byte[] bytes = data.getBytes();
	 
	                // Creating the directory to store file
	                String rootPath = servletContext.getRealPath("uploadedImages");
	                System.out.println(rootPath);
	                File dir = new File(rootPath);
	                if (!dir.exists())
	                    dir.mkdirs();
	 
	                // Create the file on server
	                File serverFile = new File(dir.getAbsolutePath()
	                        + File.separator + data.getOriginalFilename());
	                BufferedOutputStream stream = new BufferedOutputStream(
	                        new FileOutputStream(serverFile));
	                stream.write(bytes);
	                stream.close();
	 
	                //Fetch the image back
	                File imageFile = new File(serverFile.getAbsolutePath());
	                BufferedImage modifiedImage = ImageIO.read(imageFile);
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                ImageIO.write(modifiedImage, "jpg", baos);
	                baos.flush();
	                responseObject.setClickedImage(baos.toByteArray());
	                	                
	                //Converting the image to Base64 encoded string	              
	                String base64Encoded = Base64.encodeBase64String(responseObject.getClickedImage());
	                responseObject.setClickedImage(null);
	                responseObject.setBase64Img(base64Encoded);
	                
	                //Get plain text string after performing OCR
	                String extractedText = extractTextFromImage(serverFile.getAbsolutePath());
	                System.out.println("Extracted Text from Image:::"+extractedText);
	                
	                //Get keywords using Alchemy API
	                JSONObject keywordsAsJson = extractKeywordsFromText(extractedText);
	                try{
		                keywordsAsJson = keywordsAsJson.getJSONObject("results");
		                keywordsAsJson = keywordsAsJson.getJSONObject("keywords");
		                JSONArray keywordsArray = keywordsAsJson.getJSONArray("keyword");
		                finalBookList = new ArrayList<BookInfo>();
		                ArrayList<String> keywords = new ArrayList<String>();
		                for(int i=0; i<keywordsArray.length(); i++){
		                	if(finalBookList.size() < 50){
		                		JSONObject tempObj = (JSONObject)keywordsArray.get(i);
		                		String keyword = tempObj.getString("text");
			                	System.out.println("Text: " + keyword);
			                	System.out.println("Relevance: " + tempObj.getDouble("relevance"));	    
			                	//Get the books based on keywords
			                	getBooksByKeywords(keyword, keywordsArray.length());
			                	keywords.add(keyword);
		                	}else{
		                		break;
		                	}
		                }
		                
		                System.out.println("Server File Location=" + serverFile.getAbsolutePath());
		                System.out.println("Final Books List Size::::::::::::::" + finalBookList.size());
		                responseObject.setKeywords(keywords);
		                responseObject.setBooksResult(finalBookList);
		                responseObject.setSuccess("success");	                
		                System.out.println("You successfully uploaded file=" + data.getOriginalFilename()); 
		                return new ModelAndView("results","dataFromServer",responseObject);
	                } catch (JSONException je){
	                	responseObject.setError("error");
	                	return new ModelAndView("results","dataFromServer",responseObject);
	                }
	            } catch (IOException e) {
	                System.out.println("You failed to upload " + data.getOriginalFilename() + " => " + e.getMessage());
	                responseObject.setError("error");
	                return new ModelAndView("results","dataFromServer",responseObject);
	            }
	        } else {
	            System.out.println("You failed to upload " + data.getName()
                + " because the file was empty.");
	            responseObject.setError("error");
	            return new ModelAndView("results", "dataFromServer",responseObject);
	        }
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;		
	}	
	
	public String extractTextFromImage(String filePath){
		String extractedText = "";
		BytePointer outText;
        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init(null, "eng") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        // Open input image with leptonica library
        PIX image = pixRead(filePath);
        api.SetImage(image);
        // Get OCR result
        outText = api.GetUTF8Text();
        extractedText = outText.getString();
        System.out.println("OCR output:\n" + outText.getString());
        
        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);
        return extractedText;
	}
	
	public JSONObject extractKeywordsFromText(String imageText){
		AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromString("c6105a5cb456edf11b12e2c32d82ec6574d4b5ee");
		AlchemyAPI_KeywordParams params = new AlchemyAPI_KeywordParams();
		JSONObject keywordsInJson = new JSONObject();
		try {
			Document doc = alchemyObj.TextGetRankedKeywords(imageText, params);
			keywordsInJson = XML.toJSONObject(getStringFromDocument(doc));
			System.out.println("Keywords in Json::" + keywordsInJson);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keywordsInJson;
	}
	
	public String getStringFromDocument(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);

			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public void getBooksByKeywords(String searchString, int keywordsLength){
		int singleKeywordBookCount = 0;
		String url = "https://openlibrary.org/search.json?title=";
		url = url + URLEncoder.encode(searchString) + "&page=1";
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();			
			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//convert into JSONObject
			JSONObject resultJson = new JSONObject(response.toString());
			System.out.println("Books received in JSON format:::" + resultJson);
			
			//Structuring books result with keywords			
			JSONArray booksArray = null;
			try{
				booksArray = resultJson.getJSONArray("docs");
			} catch(JSONException e){
				e.printStackTrace();
			}
			for(int i=0; i<booksArray.length();i++){
				BookInfo singleBookInfo = new BookInfo();
				try{
					JSONObject singleBookInfoJson = (JSONObject)booksArray.get(i);
					singleBookInfo.setName(singleBookInfoJson.getString("title"));					
					String key = singleBookInfoJson.getString("key");
					key = key.substring(key.indexOf('O'));
					singleBookInfo.setOpenLibId(key);
					try{
						singleBookInfo.setCoverId(singleBookInfoJson.getInt("cover_i"));
					}catch(JSONException e){
						JSONArray authorNames = singleBookInfoJson.getJSONArray("author_name");
						String author = "";
						for(int j=0; j<authorNames.length();j++){
							author = author + authorNames.getString(j);
						}
						singleBookInfo.setAuthor(author);
					}					
					JSONArray authorNames = singleBookInfoJson.getJSONArray("author_name");
					String author = "";
					for(int j=0; j<authorNames.length();j++){
						author = author + authorNames.getString(j);
					}
					singleBookInfo.setAuthor(author);
				}catch(JSONException e){
					e.printStackTrace();
				}
				singleBookInfo.setAssociatedKeywords(searchString);
				int count = 0;
				System.out.println("List Size:::"+finalBookList.size());
				if(finalBookList.size() > 0 && singleBookInfo.getOpenLibId() != null && singleBookInfo.getOpenLibId().length() > 0){
					for(BookInfo b : finalBookList){
						if(b.getOpenLibId() == null || b.getOpenLibId().length() < 1){
							continue;
						}
						else if(b.getOpenLibId() == singleBookInfo.getOpenLibId()){
							String keywords = b.getAssociatedKeywords();
							keywords = keywords + ", " + singleBookInfo.getAssociatedKeywords();
							count++;
							break;
						}
					}
				}
				if(count == 0){
					finalBookList.add(singleBookInfo);
					singleKeywordBookCount++;
				}
				if(singleKeywordBookCount > 5 && keywordsLength > 10){
					break;
				}				
			}			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void getBooksByKeywords(String searchString){
		String url = "http://katalog.stbib-koeln.de:8983/solr/select?rows=20&q=";
		url = url + URLEncoder.encode(searchString);
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();			
			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			
			//convert into JSONObject
			JSONObject resultJson = XML.toJSONObject(response.toString());
			System.out.println("Books received in JSON format:::" + resultJson);
			
			//Structuring books result with keywords
			resultJson = resultJson.getJSONObject("response");
			resultJson = resultJson.getJSONObject("result");
			JSONArray booksArray = null;
			try{
				booksArray = resultJson.getJSONArray("doc");
			} catch(JSONException e){
				booksArray = new JSONArray();
				try{
					booksArray.put(resultJson.getJSONObject("doc"));
				} catch( JSONException exc){
					System.out.println("JSON Exception error");
				}
			}
			for(int i=0; i<booksArray.length();i++){
				JSONObject singleBookInfoJson = (JSONObject)booksArray.get(i);
				JSONArray tempArray = singleBookInfoJson.getJSONArray("arr");
				BookInfo singleBookInfo = new BookInfo();
				for(int j = 0; j<tempArray.length(); j++){
					JSONObject tempObj = (JSONObject)tempArray.get(j);					
					String name = tempObj.getString("name");
					if(name.equalsIgnoreCase("Author")){
						try{
							singleBookInfo.setAuthor(tempObj.get("str").toString());
						}catch(JSONException e){
							JSONArray t = tempObj.getJSONArray("str");
							String tempStr = "";
							for(int k=0; k<t.length();k++){
								tempStr = tempStr + ", " + t.get(k).toString();
							}
							singleBookInfo.setAuthor(tempStr);
						}
					}else if(name.equalsIgnoreCase("Title")){
						try{
							singleBookInfo.setName(tempObj.get("str").toString());
						}catch(JSONException e){
							JSONArray t = tempObj.getJSONArray("str");
							String tempStr = "";
							for(int k=0; k<t.length();k++){
								tempStr = tempStr + ", " + t.get(k).toString();
							}
							singleBookInfo.setName(tempStr);
						}
					}else if(name.equalsIgnoreCase("ISBN")){
						try{
							singleBookInfo.setIsbn(tempObj.get("str").toString());
						}catch(JSONException e){
							JSONArray t = tempObj.getJSONArray("str");							
							singleBookInfo.setAuthor(t.get(0).toString());
						}
					}					
				}
				singleBookInfo.setAssociatedKeywords(searchString);
				int count = 0;
				System.out.println("List Size:::"+finalBookList.size());
				if(finalBookList.size() > 0 && singleBookInfo.getIsbn() != null && singleBookInfo.getIsbn().length() > 0){
					for(BookInfo b : finalBookList){
						if(b.getIsbn() == null){
							continue;
						}
						else if(b.getIsbn().equalsIgnoreCase(singleBookInfo.getIsbn())){
							String keywords = b.getAssociatedKeywords();
							keywords = keywords + ", " + singleBookInfo.getAssociatedKeywords();
							count++;
							break;
						}
					}
				}
				if(count == 0){
					finalBookList.add(singleBookInfo);
				}
			}
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
