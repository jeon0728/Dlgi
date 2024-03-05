package com.jjh.Dlgi.board.dto

import com.jjh.Dlgi.common.status.ResultCode
import org.springframework.data.domain.Pageable

data class BoardResponse (
    val resultCode: String = ResultCode.SUCCESS.name,
    val totalCount: Long = 0,
    val data: List<BoardDtoResponse>? = null,
    val pageInfo: Pageable? = null,
    val message: String = ResultCode.SUCCESS.msg
)