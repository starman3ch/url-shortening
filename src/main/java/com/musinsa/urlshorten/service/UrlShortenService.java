package com.musinsa.urlshorten.service;

import com.musinsa.urlshorten.common.Constants;
import com.musinsa.urlshorten.domain.UrlShorten;
import com.musinsa.urlshorten.repository.UrlShortenRepository;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlShortenService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UrlShortenRepository urlShortenRepository;

    public UrlShortenService(UrlShortenRepository urlShortenRepository) {
        this.urlShortenRepository = urlShortenRepository;
    }


    public UrlShorten makeUrlShorten(String originUrl) {

        originUrl = formingUrlString(originUrl);

        checkValidUrl(originUrl);

        // 이미 등록된 short url이 있는지 확인
        Optional<UrlShorten> urlShortenOpt = urlShortenRepository.findByOriginUrl(originUrl);
        if (urlShortenOpt.isPresent()) {
            urlShortenRepository.increaseReqCount(originUrl);
            return urlShortenRepository.findByOriginUrl(originUrl).orElseThrow();
        }

        // short url 만들기
        String shortUrlCode = makeShortUrlCode(); // 중복된 Code가 나올 확률은 극히 적으므로 체크 로직은 생략함.
        UrlShorten urlShorten = new UrlShorten(shortUrlCode, originUrl, 1);
        return urlShortenRepository.save(urlShorten);
        // todo - 앱 시작하면 sql 실행되도록??


    }

    /**
     * 전달 받은 url에서 프로토콜 부분과 마지막 '/'은 없애고, 도메인만 남긴다.
     * @param url
     * @return
     */
    private String formingUrlString(String url) {
        if (url.startsWith("http://"))
            url = url.substring(7);

        if (url.startsWith("https://"))
            url = url.substring(8);

        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);

        return url;
    }


    /**
     * 유효한 Url String 인지 체크
     * @param urlString
     */
    private void checkValidUrl(String urlString) {
        UrlValidator urlValidator = new UrlValidator();
        if (!urlValidator.isValid(Constants.HTTP_PROTOCOL + urlString)) {
            throw new IllegalArgumentException(Constants.MESSAGE_NOT_VALID_URL);
        }
    }


    /**
     * 8자리 랜덤 문자열 코드를 반환
     * @return shortUrlCode
     */
    private String makeShortUrlCode() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        char[] code = new char[8];
        for (int i = 0; i < 8; i++)
            code[i] = chars.charAt((int)(Math.random() * 62));
        return String.valueOf(code);
    }

}
