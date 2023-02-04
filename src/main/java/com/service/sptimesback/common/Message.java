package com.service.sptimesback.common;

import java.util.HashMap;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@Data
public class Message {
  private int status;
  private String message;
  private HashMap<String, Object> data;

  public Message() {
    this.status = HttpStatus.BAD_REQUEST.value();
    this.data = new HashMap<String, Object>();
    this.message = null;
  }

}
