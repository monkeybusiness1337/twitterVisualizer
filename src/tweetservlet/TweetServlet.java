package tweetservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		pw.print(tweets) ;
		pw.flush() ;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
