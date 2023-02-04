package com.service.sptimesback.config;

import com.service.sptimesback.user.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Log4j2
public class SercurityConfig extends WebSecurityConfigurerAdapter{
  // 토큰을 생성하고 검증하는 컴포넌트 클래스
  private final JwtTokenProvider jwtTokenProvider;

  // remember-me 기능에 필요한 서비스 클래스
  @Autowired
  private CustomUserDetailService customUserDetailService;

  // 암호화에 필요한 PasswordEncoder 를 Bean 등록합니다.
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  // authenticationManager 를 Bean 등록합니다.
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {




    http.httpBasic().disable() //rest api 만을 고려하여 기본 설정은 해제합니다.
        .csrf().disable() //csrf 보안 토큰 disable 처리
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //토큰 기반 인증이되므로 세션 역시 사용되지 않습니다.
        .and()
        .authorizeHttpRequests() //요청에 대한 사용권한 체크
        .antMatchers("/ws/**").permitAll()
        .antMatchers("/api/login").permitAll()
        .antMatchers("/api/ldap/login").permitAll()
        .antMatchers("/api/user").permitAll()
        .antMatchers("/api/attach/**").permitAll()
        .antMatchers("/api/images/**").permitAll()
        .antMatchers("/user/**").hasAnyRole("MANAGER","ADMIN")
        .antMatchers("/api/**").hasAnyRole("MANAGER","ADMIN")
        .anyRequest().permitAll() //그외 나머지 요청은 누구나 접근 가능
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        .and()
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(corsFilter(), SecurityContextPersistenceFilter.class)
        .formLogin().disable();

    // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다.

    // remeber-me : 자동로그인 기능 설정
    http.rememberMe()
        .key("uniqueAndSecret") // 인증받은 사용자의 정보로 token을 생성하는데 사용되는 key값을 설정한다.(임의 값 설정)
        .rememberMeParameter("remember-me") // token을 생성하기 위한 파라미터
        .tokenValiditySeconds(86400 * 30) // 한달 설정
        .userDetailsService(customUserDetailService);

    // 인증하는데 필요한 UserDetailService를 넣어줘야 한다. 없다면 만들어야 한다. 필수다!

    http.logout()
        .logoutSuccessUrl("/")
        .deleteCookies("jwt")
        .invalidateHttpSession(true);

  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    log.info("build Auth global.......");

    auth.inMemoryAuthentication()
        .withUser("test")
        .password("{noop}1111")
        .roles("MANAGER");
  }

  @Bean
  public CorsFilter corsFilter(){
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowCredentials(true);
    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("*");
    source.registerCorsConfiguration("/**",configuration);
    return new CorsFilter(source);
  }
}
