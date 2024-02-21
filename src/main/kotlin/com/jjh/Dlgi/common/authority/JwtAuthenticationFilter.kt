package com.jjh.Dlgi.common.authority

import com.jjh.Dlgi.common.dto.BaseResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() { // GenericFilterBean 대상을 필터로 등록해주는 interface
    // doFilter override
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        try {
            val token = resolveToken(request as HttpServletRequest, "Authorization")
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰정보 추출하여 authentication 에 저장
                val authentication = jwtTokenProvider.getAuthentication(token)
                // SecurityContextHolder 의 getContext 에 토큰 정보 저장
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: ExpiredJwtException) {
            try {
                reissueAccessToken(request as HttpServletRequest, response as HttpServletResponse, e)
            } catch (e: Exception) {
                val errMsg = e.message
                setResponse(response as HttpServletResponse, e.message!!);
            }
        } catch (e: Exception) {
            request?.setAttribute("exception", e)
        }

        // 체인에 등록된 다음 필터 적용
        // 다음 필터가 없으면 종료
        /*try {
            chain?.doFilter(request, response)
        } catch (e: JwtException) {
            val errMsg = e.message
            setResponse(response as HttpServletResponse, e.message!!);
        }*/

        chain?.doFilter(request, response)


    }

    private fun resolveToken(request: HttpServletRequest, headerName: String): String? {
        // api 호출시 헤더에 포함된 Authorization 정보 추출 우리는 Bearer
        val bearerToken = request.getHeader(headerName)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            // 8번째 문자열부터 끝까지 잘라서 반환
            bearerToken.substring(7)
        } else {
            null
        }
    }

    private fun reissueAccessToken(request: HttpServletRequest, response: HttpServletResponse, exception: Exception) {
        try {
            val refreshToken = resolveToken(request, "Refresh-Token") ?: throw exception
            val oldAccessToken = resolveToken(request, "Authorization")!!
            jwtTokenProvider.validateRefreshToken(refreshToken, oldAccessToken)
            val newAcceesToken = jwtTokenProvider.recreateAccessToken(oldAccessToken)
            val authentication = jwtTokenProvider.getAuthentication(newAcceesToken)
            // SecurityContextHolder 의 getContext 에 토큰 정보 저장
            SecurityContextHolder.getContext().authentication = authentication

            response.setHeader("New-Access-Token", newAcceesToken)
        } catch (e: Exception) {
            request.setAttribute("exception", e)
        }
    }

    private fun setResponse(response: HttpServletResponse, msg: String): HttpServletResponse {
        response.contentType = "application/json;charset=UTF-8"
        response.status = 9999
        response.writer.print(msg)
        return response
    }
}
