package com.service.sptimesback.user.mapper;

import com.service.sptimesback.user.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@Mapper
public interface UserMapper {
  UserDto findByEmail(String userId);

  int save(UserDto userDto);
}
