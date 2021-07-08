package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Movie;
import com.example.demo.services.MovieService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
@Slf4j
public class MovieController {

	private final MovieService movieService;
	
	@GetMapping("/{id}")
	public Mono<Movie> getMovieById(@PathVariable String id) {
		log.info("MovieController getMovieById {}", id);
		// TODO Auto-generated method stub
		return movieService.getMovieById(id);
	}

	@GetMapping
	public Flux<Movie> getAllMovies() {
		// TODO Auto-generated method stub
		log.info("MovieController getAllMovies ");

		return movieService.getAllMovies();
	}
	
}
