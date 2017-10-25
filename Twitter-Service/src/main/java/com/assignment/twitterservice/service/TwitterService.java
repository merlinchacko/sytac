package com.assignment.twitterservice.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.assignment.twitterservice.exceptions.TwitterAuthenticationException;

/**
 * @author Merlin
 *
 */
public interface TwitterService {

	void streamMessages(int timeLimit, int tweetLimit, String trackParameter) throws FileNotFoundException, IOException, TwitterAuthenticationException;

}
