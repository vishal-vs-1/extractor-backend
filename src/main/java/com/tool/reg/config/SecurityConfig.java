package com.tool.reg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(c-> c.disable())
                .authorizeHttpRequests(authorize-> authorize.anyRequest().permitAll())
                .httpBasic(c-> c.disable())
                .formLogin(c-> c.disable())
                .logout(c-> c.disable());

        return http.build();
    }
}
