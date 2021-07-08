package com.example.demo.services;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.example.demo.controller.MovieController;
import com.example.demo.domain.Movie;
import com.example.demo.repositories.MovieRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

	private final MovieRepository movieRepository;
	@Override
	public Mono<Movie> getMovieById(String id) {
		// TODO Auto-generated method stub
		log.info("MovieServiceImpl getMovieById {}", id);
		log.info("MDC {}", MDC.get("x-corr-id"));
		return movieRepository.findById(id);
	}

	@Override
	public Flux<Movie> getAllMovies() {
		// TODO Auto-generated method stub
		log.info("MovieServiceImpl getAllMovies ");

		return movieRepository.findAll();
	}

}
