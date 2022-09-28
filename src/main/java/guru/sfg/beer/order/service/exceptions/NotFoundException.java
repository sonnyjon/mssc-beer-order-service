package guru.sfg.beer.order.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Sonny on 8/29/2022.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException
{
    public NotFoundException()
    {
        super();
    }

    public NotFoundException(String message)
    {
        super(message);
    }

    public NotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}