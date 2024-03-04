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
    val seq: Long? = null,

    title: String,

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
    var modDt: LocalDate?,
) {
    private fun LocalDate.formatDate(): String {
        return this.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    }

    // 원래 코틀린과 jpa 궁합이 좋지 않기 때문에
    // 코틀린으로 jpa를 사용할 때는 아래와 같이 protected set 을 이용한다.
    // 이 방법이 최선이라는거지 이 방법이 올바른 방법은 아님
    @Column(nullable = false, length = 100)
    var title: String = title
        protected set

    fun modifyTitle(title: String) {
        this.title = title
    }

    fun toDto(): BoardDtoResponse = BoardDtoResponse(title, content, regId, regDt?.formatDate(), modId, modDt?.formatDate())
}