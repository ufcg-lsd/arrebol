package org.fogbowcloud.arrebol.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidJobSpecException extends RuntimeException{

    public InvalidJobSpecException(String msg){
        super(msg);
    }
}
