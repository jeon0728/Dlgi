package com.jjh.Dlgi.board.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.jjh.Dlgi.board.entity.Board
import com.jjh.Dlgi.member.entity.Member
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate
import java.time.LocalDateTime


data class BoardDtoRequest(
    val seq: Long?,

    @field:NotBlank //빈값 허용하지 않는 어노테이션
    @JsonProperty("title") //json 요청 값과 매핑
    val title: String,

    @field:NotBlank //빈값 허용하지 않는 어노테이션
    @JsonProperty("content") //json 요청 값과 매핑
    val content: String
) {
    fun toEntity() : Board = Board(seq, title, content, null, null, null, null)
}

data class BoardSearchDtoRequest(
    val pageNum: Int,
    val pageSize: Int,
)

data class DetailSearchDtoRequest(
    val seq: Long,
)

data class BoardUpdateDtoRequest(
    val seq: Long,

    @field:NotBlank //빈값 허용하지 않는 어노테이션
    @JsonProperty("title") //json 요청 값과 매핑
    val title: String,

    @field:NotBlank //빈값 허용하지 않는 어노테이션
    @JsonProperty("content") //json 요청 값과 매핑
    val content: String,
)

data class BoardDtoResponse(
    val title: String,
    val content: String,
    val regId: String?,
    val regDt: String?,
    val modId: String?,
    val modDt: String?,
)
