package com.service.sptimesback.user.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDto implements UserDetails {
  private int id;
  private String userid;      // 로그인 아이디
  private String passwd;      // 로그인 패스워드
  private String email;       // 연락할 이메일 주소
  private String tel;         // 전화번호
  private String memo;        // 메모
  private String username;    // 사용자명
  private String nickname;    // 닉네임
  private String type;
  private String unit;
  private String[] unitArr;
  @Builder.Default
  private List<String> roles = new ArrayList<>();
  private int status;
  private String createUserId;
  private String updateUserId;
  private String createdAt;
  private String updatedAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @Override
  public String getUsername() { // 로그인할 때 쓰는 아이디
    return userid;
  }


  @Override
  public String getPassword() { // 로그인할 때 쓰는 아이디
    return passwd;
  }

  //계정 만료 여부 반환
  @Override
  public boolean isAccountNonExpired() {
    //만료되었는지 확인하는 로직
    return true; // true -> 만료되지 않았음
  }

  //계정 장금 여부 반환
  @Override
  public boolean isAccountNonLocked() {
    //계정 장금되었는지 확인하는 로직
    return true; // true -> 장금되지 않았음
  }

  //패스워드의 만료 여부 반환
  @Override
  public boolean isCredentialsNonExpired() {
    //패스워드가 만료되었는지 확인하는 로직
    return true; // true -> 만료되지 않았음
  }

  //계정 사용 가능 여부 반환
  @Override
  public boolean isEnabled() {
    //계정이 사용 가능한지 확인하는 롤직
    return true; // true -> 사용 가능
  }
}
