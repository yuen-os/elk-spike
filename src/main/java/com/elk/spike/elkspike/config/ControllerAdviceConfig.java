package com.elk.spike.elkspike.config;

import com.elk.spike.elkspike.exception.HandledServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerAdviceConfig extends ResponseEntityExceptionHandler {

    private static final String ERROR_MESSAGE = "Oops something went wrong, please contact the support team";
    private static final String MESSAGE_KEY = "message";

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> internalServerException(HttpServletRequest request, Exception ex, WebRequest webRequest) {
        Map<String, Object> errorResp = new HashMap<>();
        errorResp.put(MESSAGE_KEY, ERROR_MESSAGE);
        return ResponseEntity.status(500).body(errorResp);
    }

    @ExceptionHandler({HandledServiceException.class})
    public ResponseEntity<Object> badReqException(HttpServletRequest request, Exception ex, WebRequest webRequest) {
        Map<String, Object> errorResp = new HashMap<>();
        errorResp.put(MESSAGE_KEY, ex.getMessage());
        return ResponseEntity.status(400).body(errorResp);
    }

}
