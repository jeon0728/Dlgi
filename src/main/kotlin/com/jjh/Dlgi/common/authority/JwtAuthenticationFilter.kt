package com.jjh.Dlgi.common.authority

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() { // GenericFilterBean 대상을 필터로 등록해주는 interface
    // doFilter override
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val token = resolveToken(request as HttpServletRequest)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰정보 추출하여 authentication 에 저장
            val authentication = jwtTokenProvider.getAuthentication(token)
            // SecurityContextHolder 의 getContext 에 토큰 정보 저장
            SecurityContextHolder.getContext().authentication = authentication
        }
        // 체인에 등록된 다음 필터 적용
        // 다음 필터가 없으면 종료
        chain?.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        // api 호출시 헤더에 포함된 Authorization 정보 추출 우리는 Bearer
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            // 8번째 문자열부터 끝까지 잘라서 반환
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
