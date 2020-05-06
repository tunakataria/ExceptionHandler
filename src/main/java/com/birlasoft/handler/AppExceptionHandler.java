package com.birlasoft.handler;

import com.birlasoft.exception.ApiError;
import com.birlasoft.exception.ErrorCodeAndMessage;
import com.birlasoft.exception.ProcessingException;
import feign.FeignException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.Instant;

import static com.birlasoft.exception.ErrorCodeAndMessage.UNAUTHORIZED;


/**
 * @author Hitesh Kataria
 */

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    //Handles Exception that occurs after reaching the business layer
    @ExceptionHandler(value = {ProcessingException.class, Exception.class, FeignException.Unauthorized.class})
    protected ResponseEntity<?> handleExceptions(RuntimeException ex, WebRequest request) {
        if (ex instanceof ProcessingException) {
            return generateErrorResponse(((ProcessingException) ex).getHttpStatusCode(),
                    getApiError(((ProcessingException) ex).getErroCode(),
                            ((ProcessingException) ex).getCustomMessage(),
                            ((ServletWebRequest) request
                            ).getRequest().getRequestURI()));
        }

        if (ex instanceof FeignException.Unauthorized) {
            return generateErrorResponse(UNAUTHORIZED.getHttpCode(), getApiError(UNAUTHORIZED, ((ServletWebRequest) request
            ).getRequest().getRequestURI()));
        }
        return generateErrorResponse(500,
                getApiError("500",
                        ex.getMessage(),
                        ((WebRequest) request).getContextPath()));
    }


    //Handles the exception occurred due to binding issues, invalid parameters, un-accpetale values etc.
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        if (ex instanceof ProcessingException) {
            ProcessingException processingException = (ProcessingException) ex;
            return generateErrorResponse(processingException.getHttpStatusCode(),
                    getApiError(processingException.getErroCode(),
                            processingException.getCustomMessage(),
                            ((WebRequest) request).getContextPath()));
        }
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) ex;
            cve.getConstraintViolations().stream().forEach(constraintViolation ->
                    errorMessageBuilder.append(constraintViolation.getRootBean() + "must not be" + constraintViolation.getInvalidValue()));
            return generateErrorResponse(400, getApiError("400", errorMessageBuilder.toString(), request.getContextPath()));
        }
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException manv = (MethodArgumentNotValidException) ex;
            manv.getBindingResult().getFieldErrors().stream().forEach(fieldError -> {
                errorMessageBuilder.append(fieldError.getField()).append(" may not be ").append(fieldError.getRejectedValue()).append(System.getProperty("line.separator"));
            });
            return generateErrorResponse(400,
                    getApiError("400", errorMessageBuilder.toString(), ((ServletWebRequest) request).getRequest().getRequestURI()));
        }

        return generateErrorResponse(500,
                getApiError("500",
                        ex.getMessage(),
                        ((WebRequest) request).getContextPath()));
    }

    protected ResponseEntity<Object> generateErrorResponse(int status, Object body) {
        return ResponseEntity.status(status).body(body);
    }

    protected ApiError getApiError(String errorCode, String errorMessage, String contextPath) {
        ApiError error = ApiError.builder().errorCode(errorCode).errorMessage(errorMessage)
                .timeStamp(Instant.now()).requestPath(contextPath).build();
        return error;
    }

    protected ApiError getApiError(ErrorCodeAndMessage errorCodeAndMessage, String requesPath) {
        return getApiError(errorCodeAndMessage.getErrorCode(), errorCodeAndMessage.getErrorMessage(), requesPath);
    }

}
