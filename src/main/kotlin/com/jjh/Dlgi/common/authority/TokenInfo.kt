package com.jjh.Dlgi.common.authority

data class TokenInfo(
    val grantType: String,
    val acceesToken: String,
    val refreshToken: String
)