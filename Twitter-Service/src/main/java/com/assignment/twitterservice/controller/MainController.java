package com.assignment.twitterservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.assignment.twitterservice.service.TwitterStreamingService;


/**
 * @author Merlin
 *
 */
@Controller
@Component
public class MainController {
	
	@Autowired
	Twitter twitter;
	
	@Autowired
	TwitterStreamingService twitterStreamingService;
	
	@RequestMapping(value = "/getTweets/{hashTag}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tweet> createAccount(@PathVariable String hashTag) {
		
		return twitter.searchOperations().search(hashTag, 10).getTweets();
	}
	
	@RequestMapping("/stream/{time}")
	public String streamTweet(@PathVariable int time, Model model) throws InterruptedException{
	    Model returnedmodel = twitterStreamingService.streamApi(model, time);
	    model = returnedmodel;

	    return "stream";
	}

}
