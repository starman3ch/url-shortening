package com.musinsa.urlshorten.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;


@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class ShortUrlReq {

    @NotBlank(message = "URL을 입력하세요.")
    private String url;

}
