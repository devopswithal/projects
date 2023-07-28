package com.capstone.quotesapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "songs")
public class Song {
    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "songID")
    private Long id;

    public String getSong() { return song; }

    public void setSong(String song) {
        this.song = song;
    }

    @Column(name = "song_string")
    private String song;

}
