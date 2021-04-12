package com.musinsa.urlshorten;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.urlshorten.dto.ShortUrlReq;
import com.musinsa.urlshorten.repository.UrlShortenRepository;
import com.musinsa.urlshorten.service.UrlShortenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = UrlshortenApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@ActiveProfiles("test")
public class UrlShortenControllerTests {

//    @TestConfiguration
//    static class UrlShortenServiceContextConfiguration {
//        @Bean
//        public UrlShortenService urlShortenService() {
//            return new UrlShortenService() {
//
//            };
//        }
//    }

    // todo : given, when, then 스타일로...

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UrlShortenService urlShortenService;

    @Autowired
//    @MockBean
    private UrlShortenRepository urlShortenRepository;

    // https://www.baeldung.com/mustache // todo -

    @Test
    @DisplayName("Index 페이지 로딩")
    public void indexPageLoading() {
//        String body = this.restTemplate.getForObject("/", String.class);
//        assertThat(body).contains("URL Shorten Page");

        ResponseEntity<String> entity = restTemplate.getForEntity("/", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).contains("URL Shorten Page");

    }

    @Test
    @DisplayName("정상적으로 shortUrl을 생성해서 반환")
    public void createShortUrl() throws Exception {
        String url = "https://www.musinsa.com/";
        ShortUrlReq req = new ShortUrlReq();
        req.setUrl(url);

        mockMvc.perform(post("/shortUrl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("originUrl").value(req.getUrl()))
                .andExpect(jsonPath("shortUrl").exists())
                .andExpect(jsonPath("reqCount").exists());

//        String body = this.restTemplate.getForObject("/", String.class);
//        assertThat(body).contains("SHORTEN URL");

//        Mustache mustache = MustacheViewResolver
        // todo - 화면에 그린것까지 어떻게 테스트하나??
    }

    @Test
    @DisplayName("url 파라미터가 비어있는 경우 에러가 발생")
    public void postEmptyUrlString() throws Exception {
        mockMvc.perform(post("/shortUrl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("BAD_REQUEST"))
                .andExpect(jsonPath("message").exists());
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
    @DisplayName("요청할 때 입력한 url에 'http://'가 없는 경우 붙여줌.")
    public void httpProtocolStringNotExist() throws Exception {
        // todo
    }

    @Test
    @DisplayName("이미 요청했던 url에 대해서 같은 short url이 반환되고, Count 수는 +1이 반환됨.")
    public void returnSameShortUrl() throws Exception {
        // todo
    }




}
