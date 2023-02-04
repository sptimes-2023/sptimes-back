package com.service.sptimesback.config;

import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean{
  //토큰을 생성하고 검증하는 컴포넌트 클래스
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    //헤더에서 JWT 를 받아온다.
    String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
    //유효한 토큰인지 확인합니다.
    try{
      if (token != null && jwtTokenProvider.validateToken(token)) {
        //토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        //SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("JWT AuthFilter ##########################################################################################");
        //권한 유저 객체 가져오기
        System.out.println(authentication);
        //유저 정보 이름 가져오기
        System.out.println("UserName: " + SecurityContextHolder.getContext().getAuthentication().getName());
        //유저 roles 정보 가져오기
        System.out.println("UserRoles: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        System.out.println("JWT AuthFilter ##########################################################################################");
      }
    }catch(Exception e){
      if (e.getCause() instanceof ExpiredJwtException) {
        request.setAttribute("exception","expiredToken");
      }
      if (e instanceof IllegalArgumentException) {
        request.setAttribute("exception",null);
      }
    }
    chain.doFilter(request, response);
  }
}
