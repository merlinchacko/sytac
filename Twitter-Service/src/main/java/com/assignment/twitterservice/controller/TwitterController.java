package com.assignment.twitterservice.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.twitterservice.service.TwitterStreamingService;


/**
 * @author Merlin
 *
 */
@RestController
public class TwitterController {
	
	@Autowired
	TwitterStreamingService twitterStreamingService;
	
	@RequestMapping(value = "/stream/{timeLimit}/{tweetLimit}", method = RequestMethod.GET)
	public ResponseEntity<String> streamMessages(@PathVariable int timeLimit, @PathVariable int tweetLimit) {
		
		try {
			twitterStreamingService.streamMessages(timeLimit, tweetLimit);
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<>("Success" , HttpStatus.OK);
	}

}
