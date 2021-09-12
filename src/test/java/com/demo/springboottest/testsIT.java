package com.demo.springboottest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.demo.springboottest.controller.Library;

//file name should not have Test in end otherwise will be treated as unit test

@SpringBootTest
public class testsIT {

	// mvn test
	// two library TestRestTemplate and Rest Assured for Integration Test
	TestRestTemplate restTemplate;

	@Test
	public void getAuthoNameBooksTests() throws JSONException {

		String url = "http://localhost:8080/getBooks/author?authorname=Rahul";
		restTemplate = new TestRestTemplate();
		// here String.class is response type you want
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		System.out.println("res" + response.getStatusCode());
		System.out.println("body" + response.getBody());

		String expected = "[{\"book_name\":\"Cypress\",\"id\":\"abcd4\",\"isbn\":\"abcd\","
				+ "\"aisle\":4,\"author\":\"Rahul\"},{\"book_name\":\"Devops\",\"id\":"
				+ "\"fdsefr343\",\"isbn\":\"fdsefr3\",\"aisle\":43,\"author\":\"Rahul\"}]";

		JSONAssert.assertEquals(expected, response.getBody(), false);
		assertEquals(HttpStatus.OK, response.getStatusCode(), " 200 status successfull");
	}
	
	
	//Post HTTP Request
	@Test
	public void addBookIntegrationTest() {
		restTemplate = new TestRestTemplate();
		String url = "http://localhost:8080/addBook";

		HttpHeaders headers =new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		//construct request with payload/reqBody and headers
		HttpEntity<Library> request = new HttpEntity<Library>(buildLibrary(), headers);
		
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		String string = response.getHeaders().get("unique").get(0);
		System.out.println("header"+string);
		
	}
	
	
	// build library object
		public Library buildLibrary() {
			Library library = new Library();
			library.setAisle(332);
			library.setAuthor("sample");
			library.setIsbn("sfe");
			library.setBook_name("jackosn");
			library.setId("sfe332");
			return library;
		}
	
	

}
