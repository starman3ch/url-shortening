package com.musinsa.urlshorten.dto;

import com.musinsa.urlshorten.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ErrorRes {

    private ErrorCode code;
    private String message;

}
