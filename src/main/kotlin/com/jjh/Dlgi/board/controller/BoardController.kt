package com.jjh.Dlgi.board.controller

import com.jjh.Dlgi.board.dto.BoardDtoRequest
import com.jjh.Dlgi.board.dto.BoardDtoResponse
import com.jjh.Dlgi.board.dto.DetailSearchDtoRequest
import com.jjh.Dlgi.board.service.BoardService
import com.jjh.Dlgi.common.dto.BaseResponse
import com.jjh.Dlgi.member.dto.MemberDtoRequest
import com.jjh.Dlgi.member.dto.MemberDtoResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/board")
@RestController
class BoardController (
    private val boardService: BoardService
) {

    /**
     * 게시판 상세조회
     */
    @PostMapping("/detail")
    fun init(@RequestBody @Valid detailSearchDtoRequest: DetailSearchDtoRequest): BaseResponse<BoardDtoResponse> {
        var resultMsg = "게시글을 조회 하였습니다."
        val response = boardService.detailBoard(detailSearchDtoRequest)
        if (response == null) {
            resultMsg = "게시글을 조회하지 못하였습니다."
        }
        return BaseResponse(data = response)
    }

    /**
     * 게시판 글쓰기
     */
    @PostMapping("/regist")
    fun regist(@RequestBody @Valid boardDtoRequest: BoardDtoRequest): BaseResponse<Unit> {
        val resultMsg: String = boardService.registBoard(boardDtoRequest)
        return BaseResponse(message = resultMsg)
    }
}