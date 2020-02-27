package org.fogbowcloud.arrebol.processor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QueueNotFoundException extends IllegalArgumentException {

    public QueueNotFoundException(String s) {
        super(s);
    }
}
