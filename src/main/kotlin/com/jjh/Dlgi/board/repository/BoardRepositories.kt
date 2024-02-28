package com.jjh.Dlgi.board.repository

import com.jjh.Dlgi.board.entity.Board
import com.jjh.Dlgi.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<Board, Long> {

    fun findBySeq(seq: Long): Board?
}