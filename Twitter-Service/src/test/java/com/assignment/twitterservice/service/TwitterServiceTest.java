package com.assignment.twitterservice.service;

import static org.junit.Assert.assertFalse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.assignment.twitterservice.exceptions.TwitterAuthenticationException;
import com.assignment.twitterservice.service.serviceImpl.TwitterServiceImpl;

/**
 * @author Merlin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration
public class TwitterServiceTest {

	@Configuration
	static class TwitterServiceTestContextConfiguration {
		@Bean
		public TwitterService accountService() {
			return new TwitterServiceImpl();
		}
	}
	@Autowired
	TwitterService twitterService;
	
	private int timeLimit = 10;
	private int tweetLimit = 5;
	private String trackParameter = "Bieber";
	
	@Test
	@Ignore
	public void streamMessagesSuccessTest() throws Exception {
		
		boolean check = false;
		try {
			
			twitterService.streamMessages(timeLimit, tweetLimit, trackParameter);
		} catch(TwitterAuthenticationException e) {

			check = true;
		}
		assertFalse(check);
	}
}
