package com.jjh.Dlgi.board.service

import com.jjh.Dlgi.board.dto.BoardDtoRequest
import com.jjh.Dlgi.board.dto.BoardDtoResponse
import com.jjh.Dlgi.board.dto.BoardUpdateDtoRequest
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

    private lateinit var rstMap: Map<String, Any?>

    fun registBoard(boardDtoRequest: BoardDtoRequest): Map<String, Any?> {
        var rstMsg = "게시글이 등록 되었습니다."
        var board: Board = boardDtoRequest.toEntity()
        val now = LocalDate.now()
        val loginId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username

        board.title = boardDtoRequest.title
        board.content = boardDtoRequest.content
        board.regId = loginId
        board.regDt = now

        boardRepository.save(board)
        boardRepository.flush()

        rstMap = mapOf("board" to board, "rstMsg" to rstMsg)

        return rstMap
    }

    /**
     * 게시판 상세 조회
     */
    fun detailBoard(detailSearchDtoRequest: DetailSearchDtoRequest): Map<String, Any?> {
        var rstMsg = "조회가 완료되었습니다."
        val board: Board? = boardRepository.findBySeq(detailSearchDtoRequest.seq)
        if (board == null) {
            rstMsg = "조회가 실패하였습니다."
        }

        rstMap = mapOf("board" to board, "rstMsg" to rstMsg)

        return rstMap
    }

    /**
     * 게시판 수정
     */
    fun updateBoard(boardUpdateDtoRequest: BoardUpdateDtoRequest): Map<String, Any?> {
        var rstMsg = "수정이 완료되었습니다."
        val board: Board? = boardRepository.findBySeq(boardUpdateDtoRequest.seq)
        if (board != null) {
            val now = LocalDate.now()
            val loginId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username

            board.title = boardUpdateDtoRequest.title
            board.content = boardUpdateDtoRequest.content
            board.modId = loginId
            board.modDt = now


        } else {
            rstMsg = "수정이 실패하였습니다."
        }

        rstMap = mapOf("board" to board, "rstMsg" to rstMsg)

        return rstMap
    }
}