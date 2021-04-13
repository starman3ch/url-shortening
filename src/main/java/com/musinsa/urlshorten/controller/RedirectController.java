package com.musinsa.urlshorten.controller;

import com.musinsa.urlshorten.domain.UrlShorten;
import com.musinsa.urlshorten.repository.UrlShortenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public void redirectUrl(@PathVariable String urlCode, HttpServletResponse response) throws IOException {
        Optional<UrlShorten> urlShortenOpt = urlShortenRepository.findById(urlCode);
        if (urlShortenOpt.isPresent()) {
            response.sendRedirect(urlShortenOpt.get().getOriginUrl());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
