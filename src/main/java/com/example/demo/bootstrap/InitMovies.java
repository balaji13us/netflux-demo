package com.example.demo.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.domain.Movie;
import com.example.demo.repositories.MovieRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class InitMovies implements CommandLineRunner {
	
	private final MovieRepository movieRepository;
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		movieRepository.deleteAll()
		.thenMany(
				Flux.just("Silence of the Lambs", "James Bond", "Lord of the Rungs")
				.map(Movie::new)
				.flatMap(movieRepository::save)
				).subscribe(null, null, ()->{
					movieRepository.findAll().subscribe(System.out::println);
				});
	}

}
