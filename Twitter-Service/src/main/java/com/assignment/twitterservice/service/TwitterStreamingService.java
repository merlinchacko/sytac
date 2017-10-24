package com.assignment.twitterservice.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.assignment.twitterservice.authentication.TwitterAuthenticator;
import com.assignment.twitterservice.dto.Messages;
import com.assignment.twitterservice.dto.Users;
import com.assignment.twitterservice.exceptions.TwitterAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

/**
 * @author Merlin
 *
 */
@Service
public class TwitterStreamingService {

	private String filename = System.getProperty("user.home")+"/output.txt";
	@Value("${spring.social.twitter.app-id}")
	private String consumerKey;
	@Value("${spring.social.twitter.app-secret}")
	private String consumerSecret;

	public void streamMessages(int timeLimit, int tweetLimit) throws InterruptedException, IOException {

		List<Messages> tweets = filterStreamingData(authenticateTwitterApi(), timeLimit, tweetLimit);

		Comparator<Messages> usrComparator = (h1, h2) ->h1.getUser().getCreatedAt().compareTo(h2.getUser().getCreatedAt());
		tweets.sort(usrComparator.reversed());
		
		Map<String, List<Messages>> resultMap = new LinkedHashMap<>();
		for(Messages tweet : tweets) {
			
			String userId = tweet.getUser().getId();
			if (!resultMap.containsKey(userId)) {
				List<Messages> list = new ArrayList<Messages>();
				
			    list.add(tweet);
	
			    resultMap.put(userId, list);
			} else {
				resultMap.get(userId).add(tweet);
			}
		}

		for(List<Messages> resultList : resultMap.values()) {
			
			Comparator<Messages> msgComparator = (h1, h2) -> h1.getCreatedAt().compareTo(h2.getCreatedAt());
			resultList.sort(msgComparator.reversed());
		}
		
		resultMap.forEach((key,value)->{
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("Author : " + key);
			System.out.println();
			for(Messages tweet : value) {
				System.out.println("Message Id : "+tweet.getId()+", Message Created Date : "+tweet.getCreatedAt()+", User Created Date : "+tweet.getUser().getCreatedAt()+" User Id : "+tweet.getUser().getId()+
						", Message Text : "+tweet.getText()+", User Name : "+tweet.getUser().getName()+", User Screen Name : "+tweet.getUser().getScreenName());
			}
		});
	}

	private List<Messages> filterStreamingData(HttpRequestFactory httpRequestFactory, int timeLimit, int tweetLimit) throws IOException {
		
		List<Messages> tweets = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.getDefault());
		mapper.setDateFormat(dateFormat);
		
		HttpRequest httpRequest = httpRequestFactory.buildGetRequest(new GenericUrl("https://stream.twitter.com/1.1/statuses/filter.json?track=bieber"));
		HttpResponse response = httpRequest.execute();
		InputStream in = response.getContent();
		
		String line = null;
		int noOfTweets = 0;
		long initialTime = Instant.now().getEpochSecond();
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
		
			while ((line = reader.readLine()) != null && (noOfTweets < tweetLimit && (Instant.now().getEpochSecond() - initialTime) <= timeLimit)) {
				
				noOfTweets ++;
				tweets.add(mapper.readValue(line, Messages.class));
			}
		}
		return tweets;
	}

	private HttpRequestFactory authenticateTwitterApi() {

		HttpRequestFactory httpRequestFactory = null;
		try {
			PrintStream printStream = new PrintStream(new FileOutputStream(filename));
			TwitterAuthenticator authenticator = new TwitterAuthenticator(printStream, consumerKey, consumerSecret);

			httpRequestFactory =  authenticator.getAuthorizedHttpRequestFactory();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TwitterAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpRequestFactory;
	}

	private void trackMessagesPerSecond(long count, int noOfTweets) {
		
		System.out.println("count" + count + "nof of tweets" + noOfTweets);
		
	}

}
