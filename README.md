2024년 두번째 코틀린 프로젝트

1. kotlin + spring boot + spring security + JWT
2. BOARD CRUD API

Spring security / Jwt flow

1. 프로젝트 실행
2. security config : filterChain > jwt 필터 등록
3. api url 호출
4. JwtAuthenticationFilter : doFilter > 토큰여부 확인
5. Controller 
6. Service > UsernamePasswordAuthenticationToken 객체 생성 > 생성된 객체 authenticationToken 에 저장

	6-1. authenticationManagerBuilder.`object`.authenticate(authenticationToken) 호출 하면 내부에 있는 어떠한 메소드로 인하여
		(1). UserDetailsService 를 상속받은 CustomUserDetailsService 의 loadUserByUsername 메소드 호출
		(2). createUserDetails 메소드로 UserDetails 객체 생성 후 반환
		(3). authenticationManagerBuilder.`object`.authenticate(authenticationToken) 의 결과 값은 authenticatation 타입의 데이터

7. jwtTokenProvider : createToken(authentication)
8. 리턴값 반환


Spring Security / Jwt Error Handling

1. Controller 이전 단계에서 에러 발생 (잘못된 토큰, 유효하지 않은 토큰, 토큰 만료 등등)
2. SecurityConfig > exceptionHandling { it.authenticationEntryPoint(entryPoint) } 에 등록된 코드에 따라서
3. JwtAuthenticationEntryPoint 에 재정의된 commence 메소드 호출
4. commence 메소드 안에 선언된 resolveException 호출
5. CustomExceptionHandler 에 있는 @ExceptionHandler 을 찾아 에러처리
   
