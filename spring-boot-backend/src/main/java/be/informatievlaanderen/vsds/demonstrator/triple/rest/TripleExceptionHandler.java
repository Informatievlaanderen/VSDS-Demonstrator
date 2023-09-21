package be.informatievlaanderen.vsds.demonstrator.triple.rest;

import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TripleExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(TripleExceptionHandler.class);

    @ExceptionHandler(value = TripleFetchFailedException.class)
    protected ResponseEntity<Object> handleNotFound(RuntimeException e, WebRequest request) {
        log.error(e.getMessage());
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
