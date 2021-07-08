package com.example.demo.services;

import com.example.demo.domain.Movie;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {
	
	public Mono<Movie> getMovieById(String id);
	public Flux<Movie> getAllMovies();

}
