package tweetservlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
@WebServlet("/CallbackServlet")
public class CallbackServlet extends HttpServlet implements Servlet{
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("qABUB28LHgAo5qBAfDilreErv", "xWH51vGMrcP3YnddeF3v1D0WHQ1vdSZEmjGZ3MqXA7AtKAiotP");
		RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
		String verifier = request.getParameter("oauth_verifier");
		AccessToken accessToken = null;
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			request.getSession().removeAttribute("requestToken");
		} catch (TwitterException te) {
			throw new ServletException(te);
		}
		request.getSession().setAttribute("accessToken", accessToken.getToken().toString());
		request.getSession().setAttribute("accessTokenSecret", accessToken.getTokenSecret().toString());
		response.sendRedirect(request.getContextPath() + "/gmapssave.html");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
}
