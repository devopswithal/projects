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

    @GetMapping("/songs")
    public List<Song> getSongs(@RequestParam("search") Optional<String> searchParam){
        return searchParam.map( param->songRepository.getContainingSong(param) )
                .orElse(songRepository.findAll());
    }

    @GetMapping("/songs/{quoteId}" )
    public ResponseEntity<String> readSong(@PathVariable("songId") Long id) {
        return ResponseEntity.of(songRepository.findById(id).map(Song::getSong));
    }

    @PostMapping("/songs")
    public Song addSong(@RequestBody String song) {
        Song s = new Song();
        s.setSong(song);
        return SongRepository.save(s);
    }

    @RequestMapping(value="/song/{songId}", method=RequestMethod.DELETE)
    public void deleteSong(@PathVariable(value = "songId") Long id) {
        songRepository.deleteById(id);
    }
}
