package com.service.sptimesback.user.controller;

import com.service.sptimesback.common.Message;
import com.service.sptimesback.user.dto.UserDto;
import com.service.sptimesback.user.service.CustomUserDetailService;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class UserController {

  /**
   * The User detail service.
   */
  @Autowired
  CustomUserDetailService userDetailService;

  /**
   * The Password encoder.
   */
  @Autowired
  PasswordEncoder passwordEncoder;

  /**
   * insertUser
   *
   * @param userDto the user dto
   * @return response entity
   */
  @PostMapping("/user")
  public ResponseEntity<Message> insertUser(@RequestBody UserDto userDto){
    Message message = new Message();
    HttpHeaders headers= new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
    HttpStatus status = HttpStatus.OK;

    try {
      userDto.setPasswd(passwordEncoder.encode(userDto.getPasswd()));
      if (userDetailService.get(userDto.getUserid()) != null) {
        throw new Exception("이미 존재하는 아이디입니다.");
      }
      if(userDto.getUnitArr() != null){
        String str = Arrays.stream(userDto.getUnitArr()).collect(Collectors.joining(","));
        userDto.setUnit(str);
      }
      userDetailService.save(userDto);
      message.setStatus(200);
      message.setMessage("사용자 등록 성공!");
      message.getData().put("data",userDto); // 조회시 보낼 데이터 이렇게 넣어주세요

    } catch (Exception e) {
      System.out.println(e.getMessage());
      message.setStatus(201);
      message.setMessage("사용자 등록 실패하였습니다.");
    }

    return new ResponseEntity<>(message, headers, status);
  }
}
