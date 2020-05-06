package com.birlasoft.exception;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Data
@Builder
@Setter
@Getter
public class ApiError {
    private Instant timeStamp;
    private String errorMessage;
    private String errorCode;
    private String requestPath;
}
