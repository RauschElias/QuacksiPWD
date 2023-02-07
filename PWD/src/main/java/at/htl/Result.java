package at.htl;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

public class Result {

    Result(String message) {
        success = true;
        message = message;
    }

    Result(Set<? extends ConstraintViolation<?>> violations) {
        success = false;
        message = violations.stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.joining(", "));
    }

    private String message;
    private boolean success;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

}
