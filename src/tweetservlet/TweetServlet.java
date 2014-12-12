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
		double longitude = Double.parseDouble(request.getParameter("long")) ;
		double latitude = Double.parseDouble(request.getParameter("lat")) ;
		PrintWriter pw = response.getWriter() ;
		String tweets = this.tweetCrawler.getTweets(new Location(latitude,longitude), "asd", MediaType.PHOTO, false) ;
		HttpSession session = request.getSession(false);
		boolean checkInit = false;
		if (session != null && checkInit == false) {
			String accessToken =(String) session.getAttribute("accessToken");
			String accessTokenSecret = (String) session.getAttribute("accessTokenSecret");
			this.tweetCrawler.init(accessToken, accessTokenSecret);
			tweets = this.tweetCrawler.getPrivateTweets(new Location(latitude,longitude), "asd", MediaType.PHOTO, false);
			checkInit = true;
		} else {
			this.tweetCrawler.init();
			tweets = this.tweetCrawler.getTweets(new Location(latitude,longitude), "asd", MediaType.PHOTO, false) ;
			checkInit = true;
		}
		pw.print(tweets) ;
		pw.flush() ;
		
		// if session set and .init(..,..) wasn't executed now
		//oauthToken = session.getParameter("oauthToken") ;
		//tweetCrawler.init(oauthToken, oauthTokenSecret);
		// else flag logout set
		//tweetCrawler.init() ;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
