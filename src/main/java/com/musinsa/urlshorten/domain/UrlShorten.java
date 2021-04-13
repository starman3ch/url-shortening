package com.musinsa.urlshorten.domain;

import com.musinsa.urlshorten.common.Constants;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "shortUrlCode")
@NoArgsConstructor
@AllArgsConstructor
public class UrlShorten {

    @Id
    private String shortUrlCode;

    @Column(unique = true)
    private String originUrlDomain;

    @Column
    private int reqCount;

    public String getOriginUrl() {
        return Constants.HTTP_PROTOCOL + originUrlDomain;
    }

}