package com.jjh.Dlgi.common.authority

import com.jjh.Dlgi.common.dto.CustomUser
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
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

const val EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 60 * 12
@Component
class JwtTokenProvider(
    @Value("\${jwt.expiration-minutes}")
    private val expirationMinutes: Long,	// hours -> minutes
    @Value("\${jwt.refresh-expiration-hours}")
    private val refreshExpirationHours: Long,	// 추가
    @Value("\${jwt.issuer}")
    private val issuer: String
) {
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
            // Authentication 에 있는 권한들을 joinToString 으로 묶고
            // transform 이라는 joinToString 의 인자로
            // interface 인 GrantedAuthority 의  함수 getAuthority 를 리플랙션을 이용하여 참조 후 변수 authorities 에 반환
            .joinToString(",", transform = GrantedAuthority::getAuthority)
        val now = Date()
        // 만료시간 지정
        val accessExpiration = Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.HOURS))

        // Access Token 생성
        val accessToken = Jwts.builder()
            .setSubject(authentication.name) // 제목
            .claim("auth", authorities) // 클레임 정보(주로 인증된 사용자와 관련된 정보를 추가)
            .claim("userId", (authentication.principal as CustomUser).userId) // 클레임 정보(주로 인증된 사용자와 관련된 정보를 추가)
            .setIssuedAt(now) // 발행시간
            .setExpiration(accessExpiration) // 유효시간
            .signWith(key, SignatureAlgorithm.HS256) // 개인키를 가지고 HS512 암호화 알고리즘으로 header와 payload로 Signature를 생성.
            .compact()

        return TokenInfo("Bearer", accessToken)
    }

    fun createRefreshToken(): TokenInfo {
        val now = Date()
        val refreshExpiration = Date.from(Instant.now().plus(refreshExpirationHours, ChronoUnit.HOURS))

        // refresh Token 생성
        val refreshToken = Jwts.builder()
            .setIssuedAt(now) // 발행시간
            .setIssuer(issuer)
            .setExpiration(refreshExpiration) // 유효시간
            .signWith(key, SignatureAlgorithm.HS256) // 개인키를 가지고 HS512 암호화 알고리즘으로 header와 payload로 Signature를 생성.
            .compact()

        return TokenInfo("Bearer", refreshToken)
    }

    /**
     * token 정보 추출
     */
    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token)
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰 입니다.")
        val userId = claims["userId"] ?: throw RuntimeException("잘못된 토큰 입니다.")

        // 권한 정보 추출
        // 순회가능한 Collection 타입으로 authorities 변수 선언
        // token 에서 추출한 claims 안에 있는 auth 를 as로 이용하여 String 타입으로 명시
        // split 으로 배열 생성 후 해당 배열들의 원소(it) 를 SimpleGrantedAuthority 를 태워서 map 으로 생성 후 반환
        val authorities: Collection<GrantedAuthority> = (auth as String)
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        // claims 안에 들어있는 정보(userId, auth)에서 auth를 이용하여 추출한 권한 정보를 CustomUser 객체로 만들어
        // UserDetails 타입으로 선언한 principal이라는 변수 안에 저장
        val principal: UserDetails = CustomUser(userId.toString().toLong(), claims.subject, "", authorities)
        // 사용자의 자격증명을 기반으로 인증객체(UsernamePasswordAuthenticationToken) 생성 후 반환
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /**
     * token 검증
     * try/catch 문을 사용하여 예외처리
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

    private fun getClaims(token: String): Claims {
        var jwtParser = Jwts.parserBuilder() // JwtParserBuilder 인스턴스 생성
            .setSigningKey(key) // 키 지정
            .build() // JwtParser 반환
        var parseClaimsJws = jwtParser.parseClaimsJws(token).body // 생성된 jwtParser를 이용해 토큰 파싱 후 오리지널 Signed Jwt 반환
        return parseClaimsJws
    }

}