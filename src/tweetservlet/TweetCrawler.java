package tweetservlet;

import java.io.UnsupportedEncodingException;
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
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Query.Unit;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import twitter4j.Query.Unit;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;


public class TweetCrawler {
	public static double RADIUS = 10.0f ;
	public static Unit LENGTHUNIT = Query.KILOMETERS ;
	
	protected Twitter twitter ;
	
	// Test
	public static void main(String args[]){
		TweetCrawler ts = new TweetCrawler() ;
		String tweets = ts.getTweets(new Location(48.20732f,16.373792f), null, "photo", false) ;
		System.out.print(tweets) ;
		//ts.getTrends();
		System.out.println("\n\nCOMPARISON TEST\n\n") ;
		String expandedUrl = "youtube.com" ;
		System.out.println("expanded url contains: " + (expandedUrl.contains("youtube") ? "yep" : "nope")) ;
	}
	
	public TweetCrawler(){
		init() ;
	}
	
	
	public String getTweets(Location gps, String topic, String mediaType, boolean isHotTopic) {
		String queryString = "" ;
		Query query = new Query();
		if(isHotTopic){
			ArrayList<String> trendsArray = this.getTrendsRaw() ;
			for(int i = 0 ; i < trendsArray.size() ; i++){
				queryString += trendsArray.get(i) ;
				if(i != trendsArray.size()-1)
					queryString += " OR " ;
			}
			queryString = "("+queryString+")" + ((topic != null) ? (" AND " + topic) : "") ;
		} else{
			queryString = topic != null ? topic : "" ;
		}
		query.setCount(100) ;
		query.setQuery(queryString);
		
		GeoLocation location = new GeoLocation(gps.getLatitude(),gps.getLongitude());
		query.setGeoCode(location, RADIUS, LENGTHUNIT);
		QueryResult result;

		ArrayList<JSONObject> tweetResults = new ArrayList<JSONObject>() ;
		JSONObject jsonResult = new JSONObject() ;
		try {
			do {			
				result = this.twitter.search(query);
				List<Status> tweets = result.getTweets();
				
				if(tweets.size() == 0) break ;

				for (Status tweet : tweets) {
					if(mediaType != null && mediaType.compareTo("photo") == 0){
						ArrayList<MediaEntity> mediaEntities = new ArrayList<MediaEntity>(Arrays.asList(tweet.getMediaEntities())) ;
						for(MediaEntity mediaEntity : mediaEntities){
							if(mediaEntity.getType().compareTo(mediaType) == 0){
								tweetResults.add(getJSONGeoTweet(tweet)) ;
								break ;
							}
						}
					} else if(mediaType != null && mediaType.compareTo("video") == 0){
						if(tweetContainsVideo(tweet))
							tweetResults.add(getJSONGeoTweet(tweet)) ;
					} else if(mediaType != null && mediaType.compareTo("weblink") == 0){
						if(tweetContainsWeblink(tweet))
							tweetResults.add(getJSONGeoTweet(tweet)) ;
					} else{
						tweetResults.add(getJSONGeoTweet(tweet)) ;
					}
					
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
	
	public String getPrivateTweets(Location gps, String topic, String mediaType, boolean isHotTopic) {
		Query query = new Query("");
		GeoLocation location = new GeoLocation(gps.getLatitude(),gps.getLongitude());
		query.setGeoCode(location, RADIUS, LENGTHUNIT);
		QueryResult result;
		ArrayList<JSONObject> tweetResults = new ArrayList<JSONObject>();
		JSONObject jsonResult = new JSONObject();
		Paging paging = new Paging();
		paging.setCount(800);
		try {
			List<Status> tweets = this.twitter.getHomeTimeline(paging);
			//result = this.twitter.search(query);
			//List<Status> queryTweets = result.getTweets();
			

			for (Status tweet : tweets) {
				
				//for (Status queryTweet : queryTweets) {
					//if (queryTweet.getId() == tweet.getId()) {
					if (tweet.getGeoLocation() != null) {
						System.out.println(tweet.getId() );
						tweetResults.add(getJSONGeoTweet(tweet)) ;
					}
					//}
				//}
				
				
			}

			
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
		return jsonResult.toString();

	}
		public String getTrends(){
		JSONObject obj = new JSONObject() ;
		ArrayList<String> trendsArray = this.getTrendsRaw() ;
		try {
			obj.put("trends", trendsArray) ;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString() ;
	}
	
	public ArrayList<String> getTrendsRaw(){
		Trends trends = null ;
		ArrayList<String> trendsArray = new ArrayList<String>() ;
		
		try {
			// static place id for vienna
			// better would be depending on user location, getting the most
			// appropiate placeId and inserting it dynamically in the
			// getPlaceTrens- method
			trends = twitter.getPlaceTrends(23424750);
			
			for (int i = 0; i < trends.getTrends().length; i++) {
				trendsArray.add(trends.getTrends()[i].getName());
			}
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trendsArray ;
	}
	
	public JSONObject getJSONGeoTweet(Status tweet) throws JSONException, UnsupportedEncodingException, NoSuchAlgorithmException {
		JSONObject obj = new JSONObject() ;
		
		obj.put("id", tweet.getId()) ;
		obj.put("user", tweet.getUser().getScreenName()) ;
		obj.put("latitude", tweet.getGeoLocation().getLatitude()) ;
		obj.put("longitude", tweet.getGeoLocation().getLongitude()) ;
		obj.put("favCount", tweet.getFavoriteCount()) ;
		String parsedTweet = parse(tweet.getText()) ;
		obj.put("content", parsedTweet) ;
		
		boolean isLinkTweet = false ;
		
		if(parsedTweet.contains("<a href="))
			isLinkTweet = true ;
		

		boolean isVideoTweet = false ;
		boolean isPhotoLinked = false ;
		obj.put("urlEntities", tweet.getURLEntities().length) ;
		
		if(tweet.getURLEntities().length > 0){
			String expandedUrl = tweet.getURLEntities()[0].getExpandedURL() ;
			if(expandedUrl.contains("youtube") ||
					expandedUrl.contains("y2u.be") ||
					expandedUrl.contains("vimeo") || 
					expandedUrl.contains("youtu.be")){
						isVideoTweet = true ;
						isLinkTweet = false ;
			} else if(expandedUrl.contains("instagram")){
				isPhotoLinked = true ;
				isLinkTweet = false ;
			}
			obj.put("expandedUrl", expandedUrl) ;
		}
			
		
		
		obj.put("isPhotoLinked", isPhotoLinked) ;
		obj.put("isVideoTweet", isVideoTweet) ;
		obj.put("isLinkTweet", isLinkTweet) ;

		
		
		
		
		
		ArrayList<MediaEntity> me = new ArrayList<MediaEntity>(Arrays.asList(tweet.getMediaEntities()));
		HashMap<String,String> media = new HashMap<String,String>() ;
		
		if (me.size() > 0) {
			for (MediaEntity m : me) {
				media.put(m.getType(), m.getMediaURL()) ;
			}
		}
		
		obj.put("media", media) ;
		
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
	
	public void init(String accessToken, String accessTokenSecret){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("qABUB28LHgAo5qBAfDilreErv")
		  .setOAuthConsumerSecret("xWH51vGMrcP3YnddeF3v1D0WHQ1vdSZEmjGZ3MqXA7AtKAiotP")
		  .setOAuthAccessToken(accessToken)
		  .setOAuthAccessTokenSecret(accessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();
	}
	public static String parse(String tweetText) {
		 
	     // Search for URLs
	     if (tweetText.contains("http:")) {
	         int indexOfHttp = tweetText.indexOf("http:") ;
	         int endPoint = (tweetText.indexOf(' ', indexOfHttp) != -1) ? tweetText.indexOf(' ', indexOfHttp) : tweetText.length() ;
	         String url = tweetText.substring(indexOfHttp, endPoint) ;
	         String targetUrlHtml=  "<a href='"+url+"' target='_blank'>"+url+"</a>" ;
	         tweetText = tweetText.replace(url,targetUrlHtml) ;
	     }
	     if(tweetText.contains("https:")){
	    	 int indexOfHttp = tweetText.indexOf("https:") ;
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
	
	public static boolean tweetContainsVideo(Status tweet){
		if(tweet.getURLEntities().length > 0){
			String expandedUrl = tweet.getURLEntities()[0].getExpandedURL() ;
			boolean containsVideoUrl = (expandedUrl.contains("youtube") ||
					expandedUrl.contains("y2u.be") ||
					expandedUrl.contains("vimeo") || 
					expandedUrl.contains("youtu.be")) ;
			if(containsVideoUrl)
				return true ;
		}
		return false ;
	}
	
	public static boolean tweetContainsWeblink(Status tweet){
		String parsedTweet = parse(tweet.getText()) ;	
		String expandedUrl = "" ;
		boolean containsPhoto = false ;
		for(MediaEntity me : tweet.getMediaEntities()){
			if(me.getType().compareTo("photo") == 0){
				containsPhoto = true ;
				break ;
			}
		}
		
		if(tweet.getURLEntities().length > 0){
			expandedUrl = tweet.getURLEntities()[0].getExpandedURL() ;
		}
		
		boolean containsVideoUrl = (expandedUrl.contains("youtube") ||
				expandedUrl.contains("y2u.be") ||
				expandedUrl.contains("vimeo") || 
				expandedUrl.contains("youtu.be")) ;
		
		boolean containsPhotoUrl = expandedUrl.contains("instagram") ;
		
		if(parsedTweet.contains("<a href=") && !containsVideoUrl && !containsPhoto && !containsPhotoUrl)
			return true ;
		return false ;
	}
}
