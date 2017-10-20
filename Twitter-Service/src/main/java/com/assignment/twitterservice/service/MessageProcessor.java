package com.assignment.twitterservice.service;

import java.util.concurrent.BlockingQueue;

import org.springframework.social.twitter.api.Tweet;

/**
 * @author Merlin
 *
 */
public class MessageProcessor implements Runnable {
	
	private final BlockingQueue<Tweet> queue;

    public MessageProcessor(BlockingQueue<Tweet> queue) {
        this.queue = queue;
    }

	@Override
	public void run() {
		while (true) {
			try {
				Tweet tweet = queue.take();
				processTweet(tweet);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void processTweet(Tweet tweetEntity) {
		
		String lang = tweetEntity.getLanguageCode();
		String text = tweetEntity.getText();

		System.out.printf("%s - %s%n", lang, text);
	}


}
