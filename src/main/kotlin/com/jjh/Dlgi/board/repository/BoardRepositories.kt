package com.jjh.Dlgi.board.repository

import com.jjh.Dlgi.board.entity.Board
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<Board, Long> {

    //override fun findAll(): List<Board>

    fun findBySeq(seq: Long): Board?

    override fun findAll(pageable: Pageable): Page<Board>
}