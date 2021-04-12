package com.musinsa.urlshorten.domain;

import lombok.*;

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
    private String originUrl;
    private int reqCount;

}