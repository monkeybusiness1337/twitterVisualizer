package tweetservlet;

import twitter4j.Twitter;

public class PrivateTweetCrawler extends TweetCrawler {
	
	public PrivateTweetCrawler(Twitter twitter) {
		init(twitter);
	}
	
	public void init(Twitter twitter) {
		super.twitter = twitter;
	}
	//Cannot test because no twitter to add in constructor
	public static void main(String args[]){
		TweetCrawler ts = new TweetCrawler();
		//String tweets = ts.getTweets(new Location(48.20732f,16.373792f), "asd", MediaType.PHOTO, false) ;
		System.out.print(ts.getTrends()) ;
	}
	
}
