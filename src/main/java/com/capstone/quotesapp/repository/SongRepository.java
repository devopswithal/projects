package com.capstone.quotesapp.repository;

import com.capstone.quotesapp.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    @Query("SELECT q FROM Song q WHERE q.song LIKE %?1%")
    List<Song> getContainingSong(String word);
}
