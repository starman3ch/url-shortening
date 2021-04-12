package com.musinsa.urlshorten.controller;

import com.musinsa.urlshorten.domain.UrlShorten;
import com.musinsa.urlshorten.repository.UrlShortenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Controller
public class RedirectController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UrlShortenRepository urlShortenRepository;

    public RedirectController(UrlShortenRepository urlShortenRepository) {
        this.urlShortenRepository = urlShortenRepository;
    }

    /**
     * Shorten URL을 받으면 Origin URL로 리다이렉트
     * @param urlCode
     * @return
     */
    @GetMapping("/{urlCode}")
    public ResponseEntity redirectUrl(@PathVariable String urlCode) {
        Optional<UrlShorten> urlShortenOpt = urlShortenRepository.findById(urlCode);
        if (!urlShortenOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            URI redirectUri = new URI(urlShortenOpt.get().getOriginUrl());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
