package guru.sfg.beer.order.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Sonny on 9/26/2022.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ArgumentMismatchException extends RuntimeException
{
    public ArgumentMismatchException()
    {
        super();
    }

    public ArgumentMismatchException(String message)
    {
        super(message);
    }

    public ArgumentMismatchException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
