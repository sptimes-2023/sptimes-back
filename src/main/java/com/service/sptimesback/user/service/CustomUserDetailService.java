package com.service.sptimesback.user.service;

import com.service.sptimesback.user.dto.UserDto;
import com.service.sptimesback.user.mapper.UserMapper;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
  private final UserMapper userMapper;

  // JwtTokenProvider 에 username에 따른 UserDetails 객체 반환.(대충 디비에서 아이디 검색한다는 뜻)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDto userDbInfo = userMapper.findByEmail(username);

    // 사용자 권한을 스프링시큐리티 컨텍스트에 담아야함. singletoneList 구조로 여러개 담을 수 있게 구현.
      userDbInfo.setRoles(Collections.singletonList("ROLE_USER"));

    return userDbInfo;
  }

  /**
   * Insert user int.
   * 유저 생성
   * @param userDto the user dto
   * @return the int
   */
  public int insertUser(UserDto userDto){
    return userMapper.save(userDto);
  }
  /**
   * Get user dto.
   * 단순히 유저정보만 검색할 때 사용.
   * @param username the username
   * @return the user dto
   */
  public UserDto get(String username){
    return userMapper.findByEmail(username);
  }

  public int save(UserDto userDto){
    // 현재 사용자 정보 불러옴.
    return userMapper.save(userDto);
  }

}
