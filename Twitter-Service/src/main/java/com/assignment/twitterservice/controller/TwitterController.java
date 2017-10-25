package com.assignment.twitterservice.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.twitterservice.exceptions.TwitterAuthenticationException;
import com.assignment.twitterservice.service.TwitterService;


/**
 * @author Merlin
 *
 */
@RestController
public class TwitterController {

	@Autowired
	TwitterService twitterService;

	/**Streams tweets based on timeLimit/tweetLimit and trackParameter
	 * @param timeLimit
	 * @param tweetLimit
	 * @param trackParameter
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TwitterAuthenticationException
	 */
	@RequestMapping(value = "/stream/{timeLimit}/{tweetLimit}/{trackParameter}", method = RequestMethod.GET)
	public ResponseEntity<String> streamMessages(@PathVariable int timeLimit, @PathVariable int tweetLimit, @PathVariable String trackParameter) throws FileNotFoundException, IOException, TwitterAuthenticationException {

		twitterService.streamMessages(timeLimit, tweetLimit, trackParameter);

		return new ResponseEntity<>("Success" , HttpStatus.OK);
	}

}
