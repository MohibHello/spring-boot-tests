package com.demo.springboottest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.demo.springboottest.controller.AddResponse;
import com.demo.springboottest.controller.Library;
import com.demo.springboottest.controller.LibraryController;
import com.demo.springboottest.repository.LibraryRepository;
import com.demo.springboottest.service.LibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class SpringBootTestsApplicationTests {

	@Autowired
	LibraryController controller;

	// use MockBean annotation when testing repository
	@MockBean
	LibraryRepository repository;

	// use MockBean annotation when testing Services
	@MockBean
	LibraryService service;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	// unit test for logic
	@Test
	public void checkBuildIDLogic() {
		LibraryService lib = new LibraryService();
		String id = lib.buildId("ZMAN", 24);
		assertEquals(id, "OLDZMAN24");
		String id1 = lib.buildId("MAN", 24);
		assertEquals(id1, "MAN24");

	}

	// Test approach 1 using mockBean of mockito
	// mockito is used to mock Methods i.e., service method, repo methods, etc....
	@Test
	public void testAddBook() {
		// mock
		Library library = buildLibrary();

		// buildId and checkBookAlreadyExist in service make db request so we need to
		// mock these two to return custom value
		when(service.buildId(library.getIsbn(), library.getAisle())).thenReturn(library.getId());
		when(service.checkBookAlreadyExist(library.getId())).thenReturn(true);

		// AddResponse is a custom response class
		ResponseEntity<AddResponse> res = controller.addBookImplementation(buildLibrary());

		// check status code
		assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());

		// AddResponse is a custom response class
		AddResponse body = res.getBody();

		// check body content
		assertEquals(body.getId(), library.getId());
	}

	// Test approach 2 using mockMvc by calling service similar to postman
	// mockMvc is used to mock services, add can only be done on controllers
	// mockMvc is for controllers /POST
	@Test
	public void testAddBookByMockMvc() throws Exception {
		// mock
		Library library = buildLibrary();

		// buildId and checkBookAlreadyExist in service make db request so we need to
		// mock these two to return custom value
		// mock these methods
		when(service.buildId(library.getIsbn(), library.getAisle())).thenReturn(library.getId());
		when(service.checkBookAlreadyExist(library.getId())).thenReturn(false);

		// we can mock repo and pass any() from org.mockito.ArgumentMatchers.any;
		// package
		// any means any object passed in implementation
		when(repository.save(any())).thenReturn(library);

		// using object mapper, we need to pass string json to content method
		// in mockMvc for post we are converting here
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(library);

		// perform mock call
		// perform will help you call service it takes method and resource
		// check status
		this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON).content(jsonString))
				.andExpect(status().isCreated());

		// check response body use JsonPath("$.attribute)
		this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON).content(jsonString))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(library.getId()));

		// print response in console
		this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON).content(jsonString))
				.andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(library.getId()));

	}

	// mockMvc is for controllers /GET
	@Test
	public void testgetBookByAuthor() throws Exception {

		List<Library> li = new ArrayList<Library>();
		li.add(buildLibrary());
		li.add(buildLibrary());

		when(repository.findAllByAuthor(any())).thenReturn(li);

		this.mockMvc.perform(get("/getBooks/author").param("authorname", "TT")).andDo(print())
				.andExpect(status().isOk())
				// use is from hamcrest manual import to check length
				.andExpect(jsonPath("$.length()", is(2)))
				// to get first object from json use [0]
				.andExpect(jsonPath("$.[0].id").value("saad123"));

	}

	// mockMvc is for controllers /PUT
	@Test
	public void updateBookTest() throws Exception {
		Library library = updateLibrary();
		when(service.getBookById(any())).thenReturn(library);
		when(repository.save(any())).thenReturn(updateLibrary());

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(updateLibrary());

		// put operation testing using mockmvc
		this.mockMvc
				.perform(put("/updateBook/" + library.getId()).contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))
				.andDo(print()).andExpect(status().isOk())
				// take content() from MockMvcRequestBuilders package
				.andExpect(content().json(
						"{\"book_name\":\"samuel\",\"id\":\"saad123\",\"isbn\":\"11\",\"aisle\":222,\"author\":\"test\"}"));
	}

	// mockMvc is for controllers /DELETE
	@Test
	public void testDeleteBookById() throws Exception {
		Library library = buildLibrary();
		when(service.getBookById(any())).thenReturn(library);

		// for delete operation, delete returns void
		doNothing().when(repository).delete(buildLibrary());

		this.mockMvc
				.perform(delete("/deleteBook").contentType(MediaType.APPLICATION_JSON).content("{\"id\": \"saad123\"}"))
				.andDo(print()).andExpect(status().isCreated()).andExpect(content().string("Book is deleted"));
	}

	// build library object
	public Library buildLibrary() {
		Library library = new Library();
		library.setAisle(222);
		library.setAuthor("sample");
		library.setIsbn("sfe");
		library.setBook_name("jackosn");
		library.setId("saad123");
		return library;
	}

	// updated library object
	public Library updateLibrary() {
		Library library = new Library();
		library.setAisle(222);
		library.setAuthor("test");
		library.setIsbn("11");
		library.setBook_name("samuel");
		library.setId("saad123");
		return library;
	}

}
