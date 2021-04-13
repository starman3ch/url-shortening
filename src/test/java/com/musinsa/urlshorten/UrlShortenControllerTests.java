package com.musinsa.urlshorten;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.urlshorten.common.Constants;
import com.musinsa.urlshorten.dto.ShortUrlReq;
import com.musinsa.urlshorten.dto.ShortUrlRes;
import com.musinsa.urlshorten.repository.UrlShortenRepository;
import com.musinsa.urlshorten.service.UrlShortenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UrlShortenControllerTests {



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UrlShortenService urlShortenService;

    @Autowired
    private UrlShortenRepository urlShortenRepository;




    @BeforeEach
    public void setUp() {
        this.urlShortenRepository.deleteAll();
    }


    @Test
    @DisplayName("Index 페이지 로딩")
    public void indexPageLoading() {
        ResponseEntity<String> entity = restTemplate.getForEntity("/", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).contains("URL Shorten Page");
    }


    @ParameterizedTest
    @ValueSource(strings = { "https://www.musinsa.com/", "http://www.musinsa.com/", "www.musinsa.com/", "www.musinsa.com" })
    @DisplayName("정상적으로 shortUrl을 생성해서 반환")
    public void createShortUrl(String url) throws Exception {
        // Given

        // When
        ShortUrlReq req = new ShortUrlReq();
        req.setUrl(url);

        // Then
        mockMvc.perform(post("/shortUrl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("originUrl").value(Constants.HTTP_PROTOCOL + urlShortenService.urlDomainString(url)))
                .andExpect(jsonPath("shortUrl").exists())
                .andExpect(jsonPath("reqCount").value(1));

    }


    @Test
    @DisplayName("이미 요청했던 url에 대해서 같은 short url이 반환되고, Count 수는 +1이 반환됨.")
    public void returnSameShortUrl() throws Exception {
        // Given
        String url = "www.musinsa.com";

        // When
        ShortUrlReq req = new ShortUrlReq();
        req.setUrl(url);
        MvcResult mvcResult1 = mockMvc.perform(post("/shortUrl")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andReturn();
        MvcResult mvcResult2 = mockMvc.perform(post("/shortUrl")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andReturn();
        ShortUrlRes res1 = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), new TypeReference<ShortUrlRes>() {});
        ShortUrlRes res2 = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), new TypeReference<ShortUrlRes>() {});

        // Then
        Assertions.assertEquals(res1.getShortUrl(), res2.getShortUrl());
        Assertions.assertEquals(res1.getReqCount() + 1, res2.getReqCount());
    }


    @Test
    @DisplayName("서로 다른 url에 대해서 다른 short url을 반환됨..")
    public void returnDifferentShortUrl() throws Exception {
        // Given
        String url1 = "www.musinsa.com";
        String url2 = "www.google.com";

        // When
        ShortUrlReq req1 = new ShortUrlReq();
        req1.setUrl(url1);
        ShortUrlReq req2 = new ShortUrlReq();
        req2.setUrl(url2);
        MvcResult mvcResult1 = mockMvc.perform(post("/shortUrl")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andDo(print())
                .andReturn();
        MvcResult mvcResult2 = mockMvc.perform(post("/shortUrl")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andDo(print())
                .andReturn();
        ShortUrlRes res1 = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), new TypeReference<ShortUrlRes>() {});
        ShortUrlRes res2 = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), new TypeReference<ShortUrlRes>() {});

        // Then
        Assertions.assertNotEquals(res1.getShortUrl(), res2.getShortUrl());
        Assertions.assertEquals(res1.getReqCount(), res2.getReqCount());
    }


    @Test
    @DisplayName("url이 아닌 값을 url 파라미터로 넘겼을 경우 에러 발생")
    public void postWrongUrlString() throws Exception {
        String wrongUrl = "musinsa";
        ShortUrlReq req = new ShortUrlReq();
        req.setUrl(wrongUrl);

        mockMvc.perform(post("/shortUrl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(wrongUrl)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("BAD_REQUEST"))
                .andExpect(jsonPath("message").exists());
    }


    @Test
    @DisplayName("유효한 short url로 접속 요청")
    public void redirectSuccessWithValidShortUrl() throws Exception {

        // Given
        String url = "www.musinsa.com";
        ShortUrlReq req = new ShortUrlReq();
        req.setUrl(url);

        // When
        MvcResult mvcResult = mockMvc.perform(post("/shortUrl")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andReturn();
        ShortUrlRes shortUrlRes = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<ShortUrlRes>() {});

        // Then
        MvcResult redirectMvcResult = mockMvc.perform(get(shortUrlRes.getShortUrl()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();
        assertThat(redirectMvcResult.getResponse().getHeader("Location")).isEqualTo(shortUrlRes.getOriginUrl());
    }


    @Test
    @DisplayName("유효하지 않은 short url로 접속 요청")
    public void redirectFailedWithNotValidShortUrl() throws Exception {
        String code = "abcdefgh";
        mockMvc.perform(get("/" + code))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
