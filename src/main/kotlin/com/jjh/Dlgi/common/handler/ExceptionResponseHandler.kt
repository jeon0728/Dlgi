package com.jjh.Dlgi.common.handler

import com.jjh.Dlgi.common.dto.BaseResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import org.aspectj.bridge.Message
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionResponseHandler {
    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException() =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.")
    //BaseResponse(message = "토큰이 유효하지 않습니다.")

    @ExceptionHandler(MalformedJwtException::class)
    fun handleMalformedJwtException() =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바르지 않은 토큰입니다.")

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException() =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 만료되었습니다. 다시 로그인해주세요.")
}