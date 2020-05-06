package com.birlasoft.interceptor;


import com.birlasoft.exception.ProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class CheckValidHeadersInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CheckValidHeadersInterceptor.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        List<String> missingHeaders = missingHeaders(request);
        if (missingHeaders.size() > 0) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            ProcessingException pce = ProcessingException.builder().httpStatusCode(HttpStatus.BAD_REQUEST.value()).erroCode("400.1").
                    customMessage("Missing header " + missingHeaders.toString()).build();
            throw pce;
            //Uncomment below, if instead of throwing you want to write message into the response
            //response.getWriter().write(pce.toString());
        }
        return true;
    }

    private Map<String, String> getAllHeaderAndHeaderValues(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headersNames = request.getHeaderNames();
        while (headersNames.hasMoreElements()) {
            String headerName = headersNames.nextElement();

            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private List<String> missingHeaders(HttpServletRequest httpServletRequest) throws ProcessingException {
        Map<String, String> inHeaders = getAllHeaderAndHeaderValues(httpServletRequest);
        List<MandatoryHeaders> lOfManDatoryHeaders = Arrays.asList(MandatoryHeaders.class.getEnumConstants());
        List<String> headersNotFound = new ArrayList<>();
        lOfManDatoryHeaders.forEach(requiredHeader -> {
            String headerValue = inHeaders.get(requiredHeader.name().toLowerCase()) == null ?
                    inHeaders.get(requiredHeader.name()) :
                    inHeaders.get(requiredHeader.name().toLowerCase());
            if (headerValue == null) {
                headersNotFound.add(requiredHeader.name());
            }
        });

        return headersNotFound;
    }


    public enum MandatoryHeaders {
        env,
        fltLclOrigDt,
        Authorization,
        srcAppId,
        origOccurNbr,
        fltLegDepDt,
        opsCarrIATACd,
        opsFltNbr,
        opsLegDepArptIATACd,
        opsLegArrArptIATACd,
    }
}
