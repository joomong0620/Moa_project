package edu.og.moa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())   // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 로그인 없이 접근 허용
        return http.build();
    }
}
