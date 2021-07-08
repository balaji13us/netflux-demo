package com.example.demo.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


import com.example.demo.domain.Movie;

public interface MovieRepository extends ReactiveMongoRepository<Movie, String> {

}
