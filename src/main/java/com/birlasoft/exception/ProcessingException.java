package com.birlasoft.exception;


import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@ToString
public class ProcessingException extends RuntimeException {
    private String customMessage;
    private Throwable cause;
    private String erroCode;
    private int httpStatusCode;

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.customMessage = message;
    }

    public ProcessingException(String customMessage) {
        this.customMessage = customMessage;
        this.cause = new Throwable(customMessage);
    }
}
