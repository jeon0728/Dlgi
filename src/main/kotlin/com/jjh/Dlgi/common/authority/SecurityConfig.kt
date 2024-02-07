package com.jjh.Dlgi.common.authority

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration // Bean 등록 및 싱글톤 유지하게 해주는 annotation @Bean 과 세트
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Bean // @Configuration 과 세트
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() } // Jwt 토큰 방식을 사용하기 때문에 비활성화
            .csrf { it.disable() } // csrf 관련 설정 비활성화
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            } // Jwt 사용하기 때문에 세션 사용하지 않기
            .authorizeHttpRequests {
                it.requestMatchers("/api/member/signup").anonymous()
                    .anyRequest().permitAll()
            } // 회원가입 url 은 모든 사용자 접근 가능하도록
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider), //A
                UsernamePasswordAuthenticationFilter::class.java //B
            ) // B filter 실행 전에 A filter 실행
              // JwtAuthenticationFilter 내부 코드를 보면 성공한 경우 Authentication 객체를 생성하여 SecurityContextHolder에 저장한다.
              // 따라서 이후 실행되는 UsernamePasswordAuthenticationFilter 는 이미 인증이 완료된 상태이므로 실행되지 않는게 아니라 통과하는것이다.
        return http.build()
    }
    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}

