package com.capstone.quotesapp.controller;

import com.capstone.quotesapp.model.Movie;
import com.capstone.quotesapp.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MovieController {
    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/movies")
    public List<Movie> getMovies(@RequestParam("search") Optional<String> searchParam){
        return searchParam.map( param->movieRepository.getContainingMovie(param) )
                .orElse(movieRepository.findAll());
    }

    @GetMapping("/movies/{movieId}" )
    public ResponseEntity<String> readMovie(@PathVariable("movieId") Long id) {
        return ResponseEntity.of(movieRepository.findById(id).map(Movie::getMovie));
    }

    @PostMapping("/movies")
    public Movie addMovie(@RequestBody String movie) {
        Movie q = new Movie();
        q.setMovie(movie);
        return movieRepository.save(q);
    }

    @RequestMapping(value="/movies/{movieId}", method=RequestMethod.DELETE)
    public void deleteMovie(@PathVariable(value = "movieId") Long id) {
        movieRepository.deleteById(id);
    }
}
