package com.musinsa.urlshorten.repository;

import com.musinsa.urlshorten.domain.UrlShorten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UrlShortenRepository extends JpaRepository<UrlShorten, String> {

    Optional<UrlShorten> findByOriginUrl(String originUrl);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UrlShorten u SET u.reqCount = u.reqCount + 1 where u.originUrl=?1")
    int increaseReqCount(String originUrl);
}
