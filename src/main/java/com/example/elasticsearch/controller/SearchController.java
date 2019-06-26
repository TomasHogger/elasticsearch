package com.example.elasticsearch.controller;


import com.example.elasticsearch.model.ProfileDocument;
import com.example.elasticsearch.service.ProfileService;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class SearchController {

    @Autowired
    private ProfileService profileService;


    @GetMapping(value = "/searchTerm")
    public Terms searchTerm() throws Exception {
        return profileService.searchTerm();
    }

    @PostMapping("/create")
    public ResponseEntity createProfile(@RequestBody ProfileDocument document) throws Exception {
        return new ResponseEntity(profileService.createProfile(document), HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ProfileDocument findById(@PathVariable String id) throws Exception {
        return profileService.findById(id);
    }


    @PutMapping("/update")
    public ResponseEntity updateProfile(@RequestBody ProfileDocument document) throws Exception {
        return new ResponseEntity(profileService.updateProfile(document), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ProfileDocument> findAll() throws Exception {
        return profileService.findAll();
    }

    @DeleteMapping("/{id}")
    public String deleteProfile(@PathVariable String id)
            throws Exception {
        return profileService.deleteProfileDocument(id);
    }



}
