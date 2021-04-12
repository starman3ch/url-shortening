package com.musinsa.urlshorten.dto;

import com.musinsa.urlshorten.common.Constants;
import com.musinsa.urlshorten.domain.UrlShorten;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShortUrlRes {

    private String originUrl;
    private String shortUrl;
    private int reqCount;

    public static ShortUrlRes of(UrlShorten urlShorten) {
        ShortUrlRes shortUrlRes = new ShortUrlRes();
        shortUrlRes.originUrl = Constants.HTTP_PROTOCOL + urlShorten.getOriginUrl();
        shortUrlRes.shortUrl = Constants.SHORTEN_URL_DOMAIN + urlShorten.getShortUrlCode();
        shortUrlRes.reqCount = urlShorten.getReqCount();
        return shortUrlRes;
    }

}
