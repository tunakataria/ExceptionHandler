package com.birlasoft.exception;

import lombok.Getter;

@Getter
public enum ErrorCodeAndMessage {
    USER_NOT_FOUND(404, "404.1", "USER_NOT_FOUND"),
    USER_NAME_TAKEN(301, "301.1", "USER_NAME_TAKEN"),
    WRONG_USER_NAME_OR_PASSWORD(401, "401.1", "WRONG_USER_NAME_OR_PASSWORD"),
    MISSING_HEADER(400, "400", "HEADER Required"),
    INTERNAL_SERVER_ERROR(500, "500", "Internal Server Error"),
    NOT_FOUND(404, "404", "Data request not Found"),
    UNAUTHORIZED(401, "401", "Not Authorized"),
    FORBIDDEN(403, "403", "Forbidden"),
    NOT_IMPLEMENTED(501, "501", "Not Implemented");
    private int httpCode;
    private String errorCode;
    private String errorMessage;

    ErrorCodeAndMessage(int httpCode, String errorCode, String errorMessage) {
        this.httpCode = httpCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}