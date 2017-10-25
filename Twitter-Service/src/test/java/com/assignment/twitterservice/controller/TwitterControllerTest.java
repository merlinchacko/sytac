package com.assignment.twitterservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.assignment.twitterservice.service.TwitterService;

/**
 * @author Merlin
 *
 */
/*@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(value = TwitterController.class, secure = false)*/
public class TwitterControllerTest {

	private MockMvc mockMvc;

	@Mock
	private TwitterService twitterService;

	@InjectMocks
	private TwitterController twitterController;
	
	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(twitterController).build();
	}

	@Test
	//@Ignore
	public void streamMessagesSuccessTest() throws Exception {
		
		String resultMsg = "Success";
		
		doNothing().when(twitterService).streamMessages(any(Integer.class), any(Integer.class), any(String.class));
		
		ResponseEntity<String> result = twitterController.streamMessages(10, 20, "bieber");

		assertEquals(HttpStatus.OK, result.getStatusCode()); 
		assertEquals(resultMsg, result.getBody());
		
		verify(twitterService, times(1)).streamMessages(any(Integer.class), any(Integer.class), any(String.class));
		verifyNoMoreInteractions(twitterService);

	}

}
