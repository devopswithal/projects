package com.capstone.quotesapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {
    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movieID")
    private Long id;

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    @Column(name = "movie_string")
    private String movie;

}
