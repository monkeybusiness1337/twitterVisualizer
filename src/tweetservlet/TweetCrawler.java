package tweetservlet;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Query.Unit;
import twitter4j.conf.ConfigurationBuilder;
public class TweetCrawler {
	public static double RADIUS = 10.0f ;
	public static Unit LENGTHUNIT = Query.KILOMETERS ;
	
	private Twitter twitter ;
	
	// Test
	public static void main(String args[]){
		TweetCrawler ts = new TweetCrawler() ;
		String tweets = ts.getTweets(new Location(48.20732f,16.373792f), "asd", MediaType.PHOTO, false) ;
		System.out.print(tweets) ;
	}
	
	public TweetCrawler(){
		init() ;
	}
	
	public String getTweets(Location gps, String topic, MediaType mediaType, boolean isHotTopic) {
		Query query = new Query("");
		GeoLocation location = new GeoLocation(gps.getLatitude(),gps.getLongitude());
		query.setGeoCode(location, RADIUS, LENGTHUNIT);
		QueryResult result;

		ArrayList<JSONObject> tweetResults = new ArrayList<JSONObject>() ;
		JSONObject jsonResult = new JSONObject() ;
		try {
			do {
				result = this.twitter.search(query);
				List<Status> tweets = result.getTweets();

				for (Status tweet : tweets) {
					tweetResults.add(getJSONGeoTweet(tweet)) ;
				}

			} while ((query = result.nextQuery()) != null);
			
			jsonResult.put("satus", "sucess") ;
			jsonResult.put("result", tweetResults) ;
		} catch (TwitterException te) {
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		} catch(JSONException te){
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonResult.toString() ;
	}
	
	public JSONObject getJSONGeoTweet(Status tweet) throws JSONException, UnsupportedEncodingException, NoSuchAlgorithmException {
		JSONObject obj = new JSONObject() ;
				
		obj.put("id", tweet.getId()) ;
		obj.put("user", tweet.getUser().getScreenName()) ;
		obj.put("latitude", tweet.getGeoLocation().getLatitude()) ;
		obj.put("longitude", tweet.getGeoLocation().getLongitude()) ;
		obj.put("favCount", tweet.getFavoriteCount()) ;
		obj.put("content", this.parse(tweet.getText())) ;
		
		ArrayList<MediaEntity> me = new ArrayList<MediaEntity>(Arrays.asList(tweet.getMediaEntities()));
		HashMap<String,String> media = new HashMap<String,String>() ;
		
		if (me.size() > 0) {
			for (MediaEntity m : me) {
				media.put(m.getType(), m.getMediaURL()) ;
			}
		}
		
		obj.put("media", media) ;
		
		
		byte[] bytesOfMessage = (tweet.getUser().getScreenName()+tweet.getGeoLocation().getLatitude()+tweet.getGeoLocation().getLongitude()).getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest(bytesOfMessage);
		
		obj.put("hash", tweet.getUser().getScreenName()+tweet.getGeoLocation().getLatitude()+tweet.getGeoLocation().getLongitude()) ;

		return obj ;
	}
	
	public void init(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("PWy8QfzaVHB2ZHaZGEJEkKx5L")
		  .setOAuthConsumerSecret("M7VXYkb4eD6bF7qgTZPHrHaKdiDdQcO34ofFGw5AhtrfJ0O0x6")
		  .setOAuthAccessToken("2829252645-vpwhuEMBc6BiPKmxIhuaCl5Cxh3zozUQy2PZhE6")
		  .setOAuthAccessTokenSecret("K3lfgysmtajOShla9bZ3k0P2uP5mkjpXElx5CMlBT7p1t");
		TwitterFactory tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();
	}
	
	String parse(String tweetText) {
		 
	     // Search for URLs
	     if (tweetText.contains("http:")) {
	         int indexOfHttp = tweetText.indexOf("http:") ;
	         int endPoint = (tweetText.indexOf(' ', indexOfHttp) != -1) ? tweetText.indexOf(' ', indexOfHttp) : tweetText.length() ;
	         String url = tweetText.substring(indexOfHttp, endPoint) ;
	         String targetUrlHtml=  "<a href='"+url+"' target='_blank'>"+url+"</a>" ;
	         tweetText = tweetText.replace(url,targetUrlHtml) ;
	     }
	 
	     String patternStr = "(?:\\s|\\A)[##]+([A-Za-z0-9-_]+)" ;
	     Pattern pattern = Pattern.compile(patternStr) ;
	     Matcher matcher = pattern.matcher(tweetText) ;
	     String result = "";
	 
	     // Search for Hashtags
	     while (matcher.find()) {
	         result = matcher.group();
	         result = result.replace(" ", "");
	         String search = result.replace("#", "");
	         String searchHTML="<a href='http://search.twitter.com/search?q=" + search + "'>" + result + "</a>" ;
	         tweetText = tweetText.replace(result,searchHTML);
	     }
	 
	     // Search for Users
	     patternStr = "(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)";
	     pattern = Pattern.compile(patternStr);
	     matcher = pattern.matcher(tweetText);
	     while (matcher.find()) {
	         result = matcher.group();
	         result = result.replace(" ", "");
	         String rawName = result.replace("@", "");
	         String userHTML="<a href='http://twitter.com/"+rawName+"'>" + result + "</a>" ;
	         tweetText = tweetText.replace(result,userHTML);
	     }
	     return tweetText;
	 }
}
