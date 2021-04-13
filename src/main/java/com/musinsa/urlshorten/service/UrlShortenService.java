package com.musinsa.urlshorten.service;

import com.musinsa.urlshorten.common.Constants;
import com.musinsa.urlshorten.domain.UrlShorten;
import com.musinsa.urlshorten.repository.UrlShortenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UrlShortenService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UrlShortenRepository urlShortenRepository;

    public UrlShortenService(UrlShortenRepository urlShortenRepository) {
        this.urlShortenRepository = urlShortenRepository;
    }


    /**
     * Original Url을 가지고 Short Url을 생성후 반환함.
     * @param originUrl
     * @return UrlShorten
     */
    public UrlShorten makeUrlShorten(String originUrl) {
        String originUrlDomain = urlDomainString(originUrl);

        checkValidUrl(originUrlDomain);

        // 이미 등록된 short url이 있는지 확인
        Optional<UrlShorten> urlShortenOpt = urlShortenRepository.findByOriginUrlDomain(originUrlDomain);
        if (urlShortenOpt.isPresent()) {
            urlShortenRepository.increaseReqCount(originUrlDomain);
            return urlShortenRepository.findByOriginUrlDomain(originUrlDomain).orElseThrow(() -> new EntityNotFoundException());
        }

        // short url 만들기
        UrlShorten urlShorten =
                new UrlShorten(makeShortUrlCode(), originUrlDomain, 1); // 중복된 Code가 나올 확률은 극히 적으므로 체크 로직은 생략함.
        return urlShortenRepository.save(urlShorten);
    }


    /**
     * 전달 받은 url에서 프로토콜 부분과 마지막 '/'은 없애고, 도메인만 남긴다.
     * @param url
     * @return url domain
     */
    public String urlDomainString(String url) {
        if (url.startsWith("http://"))
            url = url.substring(7);

        if (url.startsWith("https://"))
            url = url.substring(8);

        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);

        return url;
    }


    /**
     * 유효한 Url Domain String 인지 체크
     * @param urlDomain url domain String
     */
    private void checkValidUrl(String urlDomain) {
        UrlValidator urlValidator = new UrlValidator();
        if (!urlValidator.isValid(Constants.HTTP_PROTOCOL + urlDomain)) {
            throw new IllegalArgumentException(Constants.MESSAGE_NOT_VALID_URL);
        }
    }


    /**
     * 8자리 랜덤 문자열 코드를 반환
     * @return shortUrlCode
     */
    private String makeShortUrlCode() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

}
