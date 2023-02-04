package com.service.sptimesback.user.controller;

import com.service.sptimesback.common.Message;
import com.service.sptimesback.config.JwtTokenProvider;
import com.service.sptimesback.user.dto.UserDto;
import com.service.sptimesback.user.mapper.UserMapper;
import com.service.sptimesback.user.service.CustomUserDetailService;
import java.nio.charset.Charset;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@RestController
@RequestMapping("/api")
@Log4j2
public class LoginController {
  /**
   * The Custom user detail service.
   */
  @Autowired
  CustomUserDetailService customUserDetailService;

  /**
   * The Password encoder.
   */
  @Autowired
  PasswordEncoder passwordEncoder;

  /**
   * The Jwt token provider.
   */
  @Autowired
  JwtTokenProvider jwtTokenProvider;



  @Autowired
  UserMapper userMapper;


  /**
   * JWT 로그인
   * 스프링 시큐리티 JSON기반 로그인 커스텀 구현
   *
   * @param userDto the user dto
   * @return response entity
   */
  @PostMapping("/login")
  public ResponseEntity<Message> login(@RequestBody UserDto userDto){
    Message message = new Message();
    HttpHeaders headers= new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
    HttpStatus status = HttpStatus.OK;

    try {
      UserDetails userInfo = customUserDetailService.loadUserByUsername(userDto.getUserid());
      if (userInfo == null) {
        message.setMessage("존재하지 않는 사용자입니다.");
        throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
      }
      Authentication auth = new UsernamePasswordAuthenticationToken(userDto, passwordEncoder.encode(userDto.getPassword())); // 시큐리티 기반 인증객체 생성

      if (!passwordEncoder.matches(userDto.getPassword(), userInfo.getPassword())) { // 입력된 비밀번호와 데이터베이스 비밀번호 대조
        message.setMessage("비밀번호가 틀립니다.");
        // matches : 평문, 암호문 패스워드 비교 후 boolean 결과 return
        throw new BadCredentialsException("비밀번호가 틀립니다.");
      }

        userDto.getRoles().add("USER");
      SecurityContextHolder.getContext().setAuthentication(auth); // 시큐리티 컨텍스트에 등록.
      String token = jwtTokenProvider.createToken(auth.getName(),userDto.getRoles());
      message.setStatus(200);
      message.setMessage("로그인 성공!");
      message.getData().put("data",userInfo); // 응답 시 유저정보 적재
      message.getData().put("token",token); // 응답 시 JWT 토큰 적재

    } catch(UsernameNotFoundException e){ // 여기서부터 예외처리
      log.info(e.getMessage());
      message.setStatus(201);
      message.setMessage("존재하지 않는 사용자입니다.");
    } catch(BadCredentialsException e){
      log.info(e.getMessage());
      message.setStatus(202);
      message.setMessage("비밀번호가 틀립니다.");
    } catch(NullPointerException e){
      log.info(e.getMessage());
      message.setStatus(201);
      message.setMessage("존재하지 않는 사용자입니다.");
    } catch(Exception e) {
      log.info(e.getMessage());
      message.setStatus(500);
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      message.setMessage("로그인 실패하였습니다.");
    }

    return new ResponseEntity<>(message, headers, status);
  }

}
