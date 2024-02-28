package com.jjh.Dlgi.board.entity

import com.jjh.Dlgi.board.dto.BoardDtoResponse
import com.jjh.Dlgi.member.dto.MemberDtoResponse
import jakarta.persistence.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity
@Table(uniqueConstraints = [UniqueConstraint(name = "pk_board_seq", columnNames = ["seq"])])
class Board (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var seq: Long? = null,

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = false, length = 5000)
    var content: String,

    @Column(nullable = false, length = 30, updatable = false)
    var regId: String?,

    @Column(nullable = false)
    @Temporal(TemporalType.DATE) //날짜만 입력가능하게 하는 어노테이션
    var regDt: LocalDate?,

    @Column(length = 30, updatable = false)
    var modId: String?,

    @Column
    @Temporal(TemporalType.DATE) //날짜만 입력가능하게 하는 어노테이션
    val modDt: LocalDate?,
) {
    private fun LocalDate.formatDate(): String {
        return this.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    }

    fun toDto(): BoardDtoResponse = BoardDtoResponse(title, content, regId, regDt?.formatDate(), modId, modDt?.formatDate())
}