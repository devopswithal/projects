package com.capstone.quotesapp.repository;

import com.capstone.quotesapp.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT q FROM Movie q WHERE q.movie LIKE %?1%")
    List<Movie> getContainingMovie(String word);
}
