package com.board;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

	//스프링시큐리티에서 암호화 관련 객체를 가져다가 스프링빈으로 등록
	@Bean
	public BCryptPasswordEncoder pwdEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	//스프링시큐리티 로그인 화면 사용 비활성화, CSRF/CORS 공격 방어용 보안 설정 비활성화 
	@Bean
	public SecurityFilterChain filter(HttpSecurity http) throws Exception {
		http.formLogin((login) -> login.disable())
			.csrf((csrf) -> csrf.disable())
			.cors((cors) -> cors.disable());
		return http.build();
	}
	
}
