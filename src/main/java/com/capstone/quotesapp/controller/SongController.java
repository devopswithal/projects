package com.capstone.quotesapp.controller;

import com.capstone.quotesapp.model.Song;
import com.capstone.quotesapp.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SongController {
    @Autowired
    private SongRepository songRepository;

    @GetMapping("/song")
    public List<Song> getSong(@RequestParam("search") Optional<String> searchParam){
        return searchParam.map( param->songRepository.getContainingSong(param) )
                .orElse(songRepository.findAll());
    }

    @GetMapping("/song/{quoteId}" )
    public ResponseEntity<String> readSong(@PathVariable("songId") Long id) {
        return ResponseEntity.of(songRepository.findById(id).map(Song::getSong));
    }

    @PostMapping("/song")
    public Song addSong(@RequestBody String song) {
        Song q = new Song();
        q.setSong(song);
        return quoteRepository.save(q);
    }

    @RequestMapping(value="/song/{songId}", method=RequestMethod.DELETE)
    public void deleteSong(@PathVariable(value = "songId") Long id) {
        songRepository.deleteById(id);
    }
}
