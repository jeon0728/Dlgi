package com.jjh.Dlgi.board.service

import com.jjh.Dlgi.board.dto.*
import com.jjh.Dlgi.board.entity.Board
import com.jjh.Dlgi.board.repository.BoardRepository
import com.jjh.Dlgi.common.dto.CustomUser
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Transactional
@Service
class BoardService (
    private val boardRepository: BoardRepository,
) {

    private lateinit var rstMap: Map<String, Any?>

    /**
     * 게시판 전체 조회
     */

    fun listBoard(boardSearchDtoRequest: BoardSearchDtoRequest): Map<String, Any?> {
        var rstMsg = "조회가 완료되었습니다."
        val list = boardRepository.findAll(PageRequest.of(boardSearchDtoRequest.pageNum, boardSearchDtoRequest.pageSize, Sort.by("seq").descending()))
        val pageInfo = list.pageable
        val boardInfoList = mutableListOf<BoardDtoResponse>()
        if (list == null) {
            rstMsg = "조회가 실패하였습니다."
        } else {
            list.forEach {
                boardInfoList.add(it.toDto())
            }
        }

        rstMap = mapOf("list" to boardInfoList, "rstMsg" to rstMsg, "totalCount" to list.totalElements, "pageInfo" to pageInfo)

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
     * 게시판 등록
     */
    fun registBoard(boardDtoRequest: BoardDtoRequest): Map<String, Any?> {
        var rstMsg = "게시글이 등록 되었습니다."
        var board: Board = boardDtoRequest.toEntity()
        val now = LocalDate.now()
        val loginId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username

        board.modifyTitle(boardDtoRequest.title)
        board.content = boardDtoRequest.content
        board.regId = loginId
        board.regDt = now

        boardRepository.save(board)
        boardRepository.flush()

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

            board.modifyTitle(boardUpdateDtoRequest.title)
            //board.title = boardUpdateDtoRequest.title
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