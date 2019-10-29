package dev.jgardo.jvm.miscellaneous.through.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.function.Supplier;

@SpringBootApplication
@RestController
public class ThrowExceptionSpringApplication {

    public static final String SORRY_NOT_VALID = "Sorry, not valid";

    @GetMapping("/response/with")
    public void throwException() {
        throw new ValidationException(SORRY_NOT_VALID);
    }

    @GetMapping("/response/deep/with")
    public String throwDeepException() {
        Supplier<String> supplier = () -> {
            if (true) {
                throw new ValidationException(SORRY_NOT_VALID);
            }
            return "";
        };
        return deep(100, supplier);
    }

    @GetMapping("/response/notdeep/with")
    public String throwNotDeepException() {
        Supplier<String> supplier = () -> SORRY_NOT_VALID;
        deep(100, supplier);
        throw new ValidationException(SORRY_NOT_VALID);
    }

    @GetMapping("/response/deep/without")
    public ResponseEntity<String> dontThrowDeepException() {
        return ResponseEntity.badRequest()
                .body(deep(100, () -> SORRY_NOT_VALID));
    }

    private String deep(int i, Supplier<String> producer) {
        if (i != 0) {
            return deep(i-1, producer);
        }
        return producer.get();
    }

    @GetMapping("/response/without")
    public ResponseEntity<String> dontThrowException() {
        return ResponseEntity.badRequest()
                .body(SORRY_NOT_VALID);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public String handleException(ValidationException e) {
        return SORRY_NOT_VALID;
    }

    public static void main(String[] args) {
        SpringApplication.run(ThrowExceptionSpringApplication.class);
    }
}
