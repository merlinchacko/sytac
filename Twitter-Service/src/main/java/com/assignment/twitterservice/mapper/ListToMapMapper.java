package com.assignment.twitterservice.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.assignment.twitterservice.dto.Messages;

/**
 * @author Merlin
 *
 */
public class ListToMapMapper {

	/**
	 * Convert List<Messages> to Map<String, List<Messages>> for 
	 * grouping all the users and track messages of the user
	 * @param tweets
	 * @return
	 */
	public static Map<String, List<Messages>> groupByUser(List<Messages> tweets) {
		
		Map<String, List<Messages>> map = new LinkedHashMap<>();
		for(Messages tweet : tweets) {
			
			String userId = tweet.getUser().getId();
			if (!map.containsKey(userId)) {
				List<Messages> list = new ArrayList<Messages>();
				
			    list.add(tweet);
	
			    map.put(userId, list);
			} else {
				map.get(userId).add(tweet);
			}
		}
		return map;
	}

}
