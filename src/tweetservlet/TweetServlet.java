package tweetservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/TweetServlet")
public class TweetServlet extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;
	private TweetCrawler tweetCrawler ;

    public TweetServlet() {
        
    }

    public void init() throws ServletException {
       tweetCrawler = new TweetCrawler() ;
    }

    // test coord: 48.20732f,16.373792f
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter() ;
		String tweets = null ;
		double longitude = 0.0f ;
		double latitude = 0.0f  ;
		
		if(request.getParameterMap().containsKey("long") && request.getParameterMap().containsKey("long")){
			longitude = Double.parseDouble(request.getParameter("long")) ;
			latitude = Double.parseDouble(request.getParameter("lat")) ;
		}
		
		tweets = this.tweetCrawler.getTweets(new Location(latitude,longitude), "asd", null, false) ;
		HttpSession session = request.getSession(true);
		
		String checkLogin = (String) session.getAttribute("checkLogin");
		
		if (session != null && checkLogin == "true") {
			String accessToken =(String) session.getAttribute("accessToken");
			String accessTokenSecret = (String) session.getAttribute("accessTokenSecret");
			this.tweetCrawler.init(accessToken, accessTokenSecret);
			tweets = this.tweetCrawler.getPrivateTweets(new Location(latitude,longitude), "asd", null, false);
			//session.setAttribute("checkinit", checkInit);
			pw.print(tweets) ;
			pw.flush() ;
		} else {
			//this.tweetCrawler.init();
			//tweets = this.tweetCrawler.getTweets(new Location(latitude,longitude), "asd", null , false) ;
			//session.setAttribute("checkinit", checkInit);
			
			if(request.getParameterMap().containsKey("long") && request.getParameterMap().containsKey("long")){
				longitude = Double.parseDouble(request.getParameter("long")) ;
				latitude = Double.parseDouble(request.getParameter("lat")) ;
				boolean hotTopics = request.getParameterMap().containsKey("trendTweets") ;
				boolean hasTopic = request.getParameterMap().containsKey("topic") ;
				boolean hasMediatype = request.getParameterMap().containsKey("mediaType") ;
				String topic = hasTopic ? request.getParameter("topic") : null ;
				String mediaType = hasMediatype ? request.getParameter("mediaType") : null ;
				tweets = this.tweetCrawler.getTweets(new Location(latitude,longitude), topic, mediaType, hotTopics) ;
				pw.print(tweets) ;
				pw.flush() ;
			} else if(request.getParameterMap().containsKey("getTrends")){
				String trends = this.tweetCrawler.getTrends();
				pw.print(trends) ;
				pw.flush() ;
			}
		}
	
		
		
		
		
		//String tweets = this.tweetCrawler.getTweets(new Location(latitude,longitude), "asd", MediaType.PHOTO, false) ;
		//pw.print(tweets) ;
		//pw.flush() ;
		
		// if session set and .init(..,..) wasn't executed now
		//oauthToken = session.getParameter("oauthToken") ;
		//tweetCrawler.init(oauthToken, oauthTokenSecret);
		// else flag logout set
		//tweetCrawler.init() ;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter() ;
		
		if(request.getParameterMap().containsKey("isLoggedIn")){
			HttpSession session = request.getSession(true);
			String checkLogin = (String) session.getAttribute("checkLogin");
			if(checkLogin == "true"){
				pw.write("true");
			} else{
				pw.write("false") ;
			}
			pw.flush() ;
		} else if(request.getParameterMap().containsKey("logMeOut")){
			request.getSession().invalidate() ;
		}
	}

}
