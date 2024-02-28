package com.jjh.Dlgi.board.service

import com.jjh.Dlgi.board.dto.BoardDtoRequest
import com.jjh.Dlgi.board.dto.BoardDtoResponse
import com.jjh.Dlgi.board.dto.DetailSearchDtoRequest
import com.jjh.Dlgi.board.entity.Board
import com.jjh.Dlgi.board.repository.BoardRepository
import com.jjh.Dlgi.common.dto.CustomUser
import com.jjh.Dlgi.common.exception.InvalidInputException
import com.jjh.Dlgi.member.dto.MemberDtoResponse
import com.jjh.Dlgi.member.entity.Member
import com.jjh.Dlgi.member.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Transactional
@Service
class BoardService (
    private val boardRepository: BoardRepository,
) {

    fun registBoard(boardDtoRequest: BoardDtoRequest): String {
        val rstMsg = "게시글이 등록 되었습니다."
        var board: Board = boardDtoRequest.toEntity()
        val now = LocalDate.now()
        val loginId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username

        board.title = boardDtoRequest.title
        board.content = boardDtoRequest.content
        board.regId = loginId
        board.regDt = now

        boardRepository.save(board)
        boardRepository.flush()

        return rstMsg
    }

    /**
     * 정보 조회
     */
    fun detailBoard(detailSearchDtoRequest: DetailSearchDtoRequest): BoardDtoResponse {
        val board: Board = boardRepository.findBySeq(detailSearchDtoRequest.seq) ?: throw InvalidInputException("seq", "게시글(${detailSearchDtoRequest.seq}이 존재하지 않는 유저입니다.)")
        return board.toDto()
    }
}