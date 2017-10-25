package com.assignment.twitterservice.service.serviceImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.assignment.twitterservice.authentication.TwitterAuthenticator;
import com.assignment.twitterservice.dto.Messages;
import com.assignment.twitterservice.exceptions.TwitterAuthenticationException;
import com.assignment.twitterservice.mapper.ListToMapMapper;
import com.assignment.twitterservice.service.TwitterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

/**
 * @author Merlin
 *
 */
@Service("twitterService")
public class TwitterServiceImpl implements TwitterService {

	private final String twitterUrl = "https://stream.twitter.com/1.1/statuses/filter.json?track=";
	private String filename = System.getProperty("user.home")+"/output.txt";
	@Value("${spring.social.twitter.app-id}")
	private String consumerKey;
	@Value("${spring.social.twitter.app-secret}")
	private String consumerSecret;
	private PrintStream printStream;

	/* (non-Javadoc)
	 * @see com.assignment.twitterservice.service.TwitterService#streamMessages(int, int, java.lang.String)
	 * Stream incoming twitter messages track with trackParameter for timeLimit seconds or first tweetLimit messages and 
	 * print the messages grouped by user(bot users and messages are sorted chronologically)
	 */
	public void streamMessages(int timeLimit, int tweetLimit, String trackParameter) throws FileNotFoundException, IOException, TwitterAuthenticationException {

		List<Messages> tweets = filterStreamingData(authenticateTwitterApi(), timeLimit, tweetLimit, trackParameter);
		
		sortAndGroupTweetList(tweets);
	}

	/**
	 * First sort the list of Messages based on users, chronologically ascending,
	 * then the result is grouped by user id and set to a linked hashmap.
	 * Finally the result is again sorted based on messages, chronologically ascending.
	 * And sorted list of messages is logged in STDOUT and output.txt file
	 * @param tweets
	 */
	private void sortAndGroupTweetList(List<Messages> tweets) {
		
		Comparator<Messages> usrComparator = (h1, h2) -> h1.getUser().getCreatedAt().compareTo(h2.getUser().getCreatedAt());
		Comparator<Messages> msgComparator = (h1, h2) -> h1.getCreatedAt().compareTo(h2.getCreatedAt());
		
		tweets.sort(usrComparator.reversed());
		
		Map<String, List<Messages>> resultMap = ListToMapMapper.groupByUser(tweets);
		
		for(List<Messages> resultList : resultMap.values()) {
			
			resultList.sort(msgComparator.reversed());
		}
		
		printOutputList(resultMap);
	}

	/**
	 * Iterate the resultMap and print the author and message details in a format to a file and console.
	 * @param resultMap
	 */
	private void printOutputList(Map<String, List<Messages>> resultMap) {
		
		resultMap.forEach((key,value)->{
			printStream.println("-----------------------------------------------------------------------------------------------------------------------------------------------");
			printStream.println("Author : " + key);
			printStream.println();
			System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Author : " + key);
			System.out.println();
			for(Messages tweet : value) {
				printStream.println("Message Id : "+tweet.getId()+", Message Created Date : "+tweet.getCreatedAt()+", User Created Date : "+tweet.getUser().getCreatedAt()+" User Id : "+tweet.getUser().getId()+
						", Message Text : "+tweet.getText()+", User Name : "+tweet.getUser().getName()+", User Screen Name : "+tweet.getUser().getScreenName());
				System.out.println("Message Id : "+tweet.getId()+", Message Created Date : "+tweet.getCreatedAt()+", User Created Date : "+tweet.getUser().getCreatedAt()+" User Id : "+tweet.getUser().getId()+
						", Message Text : "+tweet.getText()+", User Name : "+tweet.getUser().getName()+", User Screen Name : "+tweet.getUser().getScreenName());
			}
		});
	}

	/**
	 * Stream the twitter data using Twitter Streaming API which tracks "trackParameter"
	 * Used jackson object mapper to convert the Json data to Messages object.
	 * Inputstream from response is read until timeLimit or count of tweetLimit is reached 
	 * and added to a list of Messages object
	 * @param httpRequestFactory
	 * @param timeLimit
	 * @param tweetLimit
	 * @param trackParameter
	 * @return
	 * @throws IOException
	 */
	private List<Messages> filterStreamingData(HttpRequestFactory httpRequestFactory, int timeLimit, int tweetLimit, String trackParameter) throws IOException {
		
		List<Messages> tweets = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.getDefault());
		mapper.setDateFormat(dateFormat);
		
		try {
			HttpRequest httpRequest = httpRequestFactory.buildGetRequest(new GenericUrl(twitterUrl+trackParameter));
			HttpResponse response = httpRequest.execute();
			InputStream in = response.getContent();
			
			String line = null;
			int noOfTweets = 0;
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
				
				long initialTime = Instant.now().getEpochSecond();
				while ((line = reader.readLine()) != null && (noOfTweets < tweetLimit && (Instant.now().getEpochSecond() - initialTime) <= timeLimit)) {
					
					noOfTweets ++;
					printStream.println("In "+(Instant.now().getEpochSecond() - initialTime)+" seconds, "+noOfTweets+" messages");
					System.out.println("In "+(Instant.now().getEpochSecond() - initialTime)+" seconds, "+noOfTweets+" messages");
					tweets.add(mapper.readValue(line, Messages.class));
				}
				long finalTime = Instant.now().getEpochSecond();
				trackMessagesPerSecond((finalTime - initialTime), noOfTweets);
			}
		} catch (IOException e1) {
			
			System.out.println("IOException : "+e1.getMessage());
			throw e1;
		}
		return tweets;
	}

	/**
	 * Provide access to the Twitter API by OAuth flow using consumerKey and consumerSecret
	 * @return
	 * @throws FileNotFoundException
	 * @throws TwitterAuthenticationException
	 */
	private HttpRequestFactory authenticateTwitterApi() throws FileNotFoundException, TwitterAuthenticationException {

		HttpRequestFactory httpRequestFactory = null;
		try {
			printStream = new PrintStream(new FileOutputStream(filename));
			TwitterAuthenticator authenticator = new TwitterAuthenticator(printStream, consumerKey, consumerSecret);

			httpRequestFactory =  authenticator.getAuthorizedHttpRequestFactory();
		} catch (FileNotFoundException e1) {
			
			System.out.println("FileNotFoundException : "+e1.getMessage());
			throw e1;
		} catch (TwitterAuthenticationException e) {
			
			System.out.println("TwitterAuthenticationException : "+e.getMessage());
			throw e;
		}
		return httpRequestFactory;
	}

	/**
	 * Calculate number of messages per second
	 * @param time
	 * @param noOfTweets
	 */
	private void trackMessagesPerSecond(long time, int noOfTweets) {
		
		DecimalFormat df = new DecimalFormat("#.##"); 
		double noOfTweetsPerSecond = (double) (noOfTweets/time);

		printStream.println("Tracking number of messages per second in each run of application : "+df.format(noOfTweetsPerSecond)+" messages");
		System.out.println("Tracking number of messages per second in each run of application : "+df.format(noOfTweetsPerSecond)+" messages");
		
	}

}
