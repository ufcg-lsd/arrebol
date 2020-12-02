/* (C)2020 */
package org.fogbowcloud.arrebol.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class JobNotFoundException extends RuntimeException {

  public JobNotFoundException(String message) {
    super(message);
  }
}
