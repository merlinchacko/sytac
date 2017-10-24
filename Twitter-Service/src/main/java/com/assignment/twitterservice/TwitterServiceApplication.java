package com.assignment.twitterservice;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootApplication
public class TwitterServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TwitterServiceApplication.class, args);
	}
	
	public void run(String... arg0) throws Exception {

		//create a file in user home

		File file = new File(System.getProperty("user.home")+"/output.txt");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://localhost:8080/stream/{timeLimit}/{tweetLimit}";

		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("timeLimit", "30");
		uriParams.put("tweetLimit", "100");

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(builder.buildAndExpand(uriParams).toUri() , HttpMethod.GET, entity, String.class);

		System.out.println("--------------response status-------------->"+response.getStatusCode());
	}
}
