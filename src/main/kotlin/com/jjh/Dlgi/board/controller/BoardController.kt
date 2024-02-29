package com.jjh.Dlgi.board.controller

import com.jjh.Dlgi.board.dto.BoardDtoRequest
import com.jjh.Dlgi.board.dto.BoardDtoResponse
import com.jjh.Dlgi.board.dto.BoardUpdateDtoRequest
import com.jjh.Dlgi.board.dto.DetailSearchDtoRequest
import com.jjh.Dlgi.board.entity.Board
import com.jjh.Dlgi.board.service.BoardService
import com.jjh.Dlgi.common.dto.BaseResponse
import com.jjh.Dlgi.common.status.ResultCode
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
    fun detail(@RequestBody @Valid detailSearchDtoRequest: DetailSearchDtoRequest): BaseResponse<BoardDtoResponse> {
        val rstMap = boardService.detailBoard(detailSearchDtoRequest)
        val boardInfo = (rstMap.get("board") as Board).toDto()
        return BaseResponse(data = boardInfo, message = rstMap.get("rstMsg") as String)
    }

    /**
     * 게시판 글쓰기
     */
    @PostMapping("/regist")
    fun regist(@RequestBody @Valid boardDtoRequest: BoardDtoRequest): BaseResponse<BoardDtoResponse> {
        val rstMap = boardService.registBoard(boardDtoRequest)
        val boardInfo = (rstMap.get("board") as Board).toDto()
        return BaseResponse(data = boardInfo, message = rstMap.get("rstMsg") as String)
    }

    /**
     * 게시판 수정하기
     */
    @PostMapping("/update")
    fun update(@RequestBody @Valid boardUpdateDtoRequest: BoardUpdateDtoRequest): BaseResponse<BoardDtoResponse> {
        val rstMap = boardService.updateBoard(boardUpdateDtoRequest)
        val boardInfo = (rstMap.get("board") as Board).toDto()
        return BaseResponse(data = boardInfo, message = rstMap.get("rstMsg") as String)
    }
}