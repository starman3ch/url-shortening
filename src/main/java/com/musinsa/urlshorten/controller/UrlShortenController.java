package com.musinsa.urlshorten.controller;

import com.musinsa.urlshorten.common.ErrorCode;
import com.musinsa.urlshorten.dto.ErrorRes;
import com.musinsa.urlshorten.dto.ShortUrlReq;
import com.musinsa.urlshorten.dto.ShortUrlRes;
import com.musinsa.urlshorten.domain.UrlShorten;
import com.musinsa.urlshorten.service.UrlShortenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequestMapping("/")
public class UrlShortenController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UrlShortenService urlShortenService;

    public UrlShortenController(UrlShortenService urlShortenService) {
        this.urlShortenService = urlShortenService;
    }


    /**
     * 인덱스 화면
     * @return
     */
    @GetMapping
    public String getUrlShortenPage() {
        return "index";
    }


    /**
     * Short Url을 생성
     * @param req
     * @return
     */
    @PostMapping("/shortUrl")
    @ResponseBody
    public ShortUrlRes makeUrlShorten(@Valid @RequestBody ShortUrlReq req) {
        UrlShorten urlShorten = urlShortenService.makeUrlShorten(req.getUrl());
        return ShortUrlRes.of(urlShorten);
    }



    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorRes illegalArgumentExceptionHandler(IllegalArgumentException e) {
        return new ErrorRes(ErrorCode.BAD_REQUEST, e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorRes methodArgumentExceptionHandler(MethodArgumentNotValidException e) {
        return new ErrorRes(ErrorCode.BAD_REQUEST, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

}
