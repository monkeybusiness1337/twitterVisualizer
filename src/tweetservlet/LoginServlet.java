package tweetservlet;

import java.io.IOException;
//import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
//import twitter4j.conf.ConfigurationBuilder;


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet implements Servlet{
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			TwitterFactory tf = new TwitterFactory();
			Twitter twitter = tf.getInstance();
			RequestToken requestToken;
			request.getSession().setAttribute("twitter", twitter);
			twitter.setOAuthConsumer("qABUB28LHgAo5qBAfDilreErv", "xWH51vGMrcP3YnddeF3v1D0WHQ1vdSZEmjGZ3MqXA7AtKAiotP");
			StringBuffer callbackURL = request.getRequestURL();
			int index = callbackURL.lastIndexOf("/");
			callbackURL.replace(index, callbackURL.length(), "").append("/CallbackServlet");
			System.out.println(callbackURL.toString());
			requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
			String authURL = requestToken.getAuthenticationURL();
			request.getSession().setAttribute("requestToken", requestToken);
			response.sendRedirect(authURL);
			
			
		} catch (TwitterException te) {
			// TODO Auto-generated catch block
			te.printStackTrace();
		}
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
}
