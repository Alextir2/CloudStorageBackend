package alex.tir.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "A file system exception has occurred";

    public FileException() {
        super(DEFAULT_MESSAGE);
    }

    public FileException(String message) {
        super(message);
    }

    public FileException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

}
