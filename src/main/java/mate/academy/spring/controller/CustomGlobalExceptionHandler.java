package mate.academy.spring.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import mate.academy.spring.exception.DataProcessingException;
import mate.academy.spring.util.DateTimePatternUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final DateTimeFormatter FORMAT_PATTERN = DateTimeFormatter
                            .ofPattern(DateTimePatternUtil.DATE_TIME_PATTERN);

    @ExceptionHandler({DataProcessingException.class})
    protected ResponseEntity<Object> handleExceptionInternal(RuntimeException ex) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> respBody = new LinkedHashMap<>();
        respBody.put("date", now.format(FORMAT_PATTERN));
        respBody.put("status", 500);
        respBody.put("message", ex.getMessage());
        return new ResponseEntity<>(respBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("date", now.format(FORMAT_PATTERN));
        body.put("status", status.value());
        List<Object> errors = ex.getBindingResult().getAllErrors()
                                                    .stream()
                                                    .map(this::getErrorMessage)
                                                    .collect(Collectors.toList());
        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            return "Field: " + field + ", " + e.getDefaultMessage();
        }
        return e.getDefaultMessage();
    }
}
