package com.jjh.Dlgi.common.dto

import com.jjh.Dlgi.common.status.ResultCode

data class BaseResponse<T> (
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String = ResultCode.SUCCESS.msg
)