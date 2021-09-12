package com.demo.springboottest.repository;

import java.util.List;

import com.demo.springboottest.controller.Library;

public interface LibraryRepositoryCustom {
	
	List<Library> findAllByAuthor(String authorName);

}
