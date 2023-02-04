package com.service.sptimesback.config;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@Component
@Log4j2
public class CustomAuthenticationEntryPoint  implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    String exception = (String)request.getAttribute("exception");

    log.debug("log: exception: {} ", exception);

    /**
     * 토큰 없는 경우
     */
    if(exception == null) {

      setResponse(response, 000);
      return;
    }

    /**
     * 토큰 만료된 경우
     */
    if(exception.equals("expiredToken")) {
      setResponse(response, 111);
      return;
    }

    /**
     * 토큰 시그니처가 다른 경우
     */
    if(exception.equals("3") ){
      setResponse(response, 3);
    }
  }

  /**
   * 한글 출력을 위해 getWriter() 사용
   */
  private void setResponse(HttpServletResponse response, int errorCode) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    if (errorCode == 000) {
      response.getWriter().write(
          "{ \"message\" : \"" + "로그인 정보가 없습니다."
              + "\", \"status\" : " + "201" + "}");
      response.setStatus(200); // http response는 성공으로 표시
    } else if (errorCode == 111) {
      response.getWriter().write(
          "{ \"message\" : \"" + "로그인 정보가 만료되었습니다."
              + "\", \"status\" : " + errorCode + "}"); //
      response.setStatus(200); // http response는 성공으로 표시
    }

  }
}
