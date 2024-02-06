package com.jjh.Dlgi.common.authority

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

const val EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 60 * 12

class JwtTokenProvider {
    @Value("\${jwt.secret}") // yml 파일에 있는 프로퍼티 바인딩
    lateinit var secretKey: String
    // by lazy
    // key 라는 값을 나중에 초기화 해주겠다는 의미 lateinit 과 같은 개념
    // 보통 by lazy 를 사용하는 경우는 lateinit 과 같이 사용하는 경우가 많다.
    // by lazy 내부 코드를 보면 secretKey 값을 이용하고 있다.
    // 근데 secretKey 도 lateinit 으로 늦은 초기화를 하고 있어서 그 값이 명확치 않기 떄문에
    // secretKey 의 값이 정해지면 key 라는 변수의 값도 정하겠다는 뜻이다.
    private val key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    /**
     * token 생성
     */
    fun createToken(authentication: Authentication): TokenInfo {
        // 권한들을 ',' 로 이어서 문자열로 생성한다음 authorities 에 저장
        val authorities: String = authentication
            .authorities
            .joinToString(",", transform = GrantedAuthority::getAuthority)
        val now = Date()
        // 만료시간 지정
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        // Access Token 생성
        val accessToken = Jwts.builder()
            .setSubject(authentication.name) // 제목
            .claim("auth", authorities) // 클레임 정보(주로 인증된 사용자와 관련된 정보를 추가)
            .setIssuedAt(now) // 발행시간
            .setExpiration(accessExpiration) // 유효시간
            .signWith(key, SignatureAlgorithm.HS256) // 개인키를 가지고 HS512 암호화 알고리즘으로 header와 payload로 Signature를 생성.
            .compact()

        return TokenInfo("Bearer", accessToken)
    }

    /**
     * token 정보 추출
     */
    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token)
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰 입니다.")

        //권한 정보 추출
        val authorities: Collection<GrantedAuthority> = (auth as String)
            .split(",")
            .map { SimpleGrantedAuthority(it) }
        val principal: UserDetails = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /**
     * token 검증
     */
    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {} // Invalid JWT Token
                is MalformedJwtException -> {} // Invalid JWT Token
                is ExpiredJwtException -> {} // Expired JWT Token
                is IllegalArgumentException -> {} // Unsupported JWT Token
                is UnsupportedJwtException -> {} // JWT claims string is empty
                else -> {}  // else
            }
            println(e.message)
        }
        return false
    }

    private fun getClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
}