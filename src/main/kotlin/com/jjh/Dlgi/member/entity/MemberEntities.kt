package com.jjh.Dlgi.member.entity

import com.jjh.Dlgi.common.status.Gender
import com.jjh.Dlgi.common.status.ROLE
import com.jjh.Dlgi.member.dto.MemberDtoResponse
import jakarta.persistence.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity
@Table(uniqueConstraints = [UniqueConstraint(name = "uk_member_login_id", columnNames = ["loginId"])])
class Member (
    @Id
    @Column(nullable = false, length = 30, updatable = false)
    val loginId: String,

    @Column(nullable = false, length = 100)
    val password: String,

    @Column(nullable = false, length = 10)
    var name: String,

    @Column(nullable = false)
    @Temporal(TemporalType.DATE) //날짜만 입력가능하게 하는 어노테이션
    val birthDate: LocalDate,

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @Column(nullable = false, length = 30)
    val email: String,
) {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    // Member 테이블을 조회할 때 조인되는 테이블인 MemberRole 의 정보가 필요하지 않을 수도 있다.
    // FetchType.LAZY 은 데이터가 필요한 시점에 사용하기 위하여 연관된 객체에 프록시 객체를 넣어둔다.
    // 실제 데이터가 필요한 순간이 되어서야 데이터베이스를 조회해서 프록시 객체를 초기화함.
    val memberRole: List<MemberRole>? = null

    private fun LocalDate.formatDate(): String {
        return this.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    }

    fun toDto(): MemberDtoResponse = MemberDtoResponse(loginId, name, birthDate.formatDate(), gender.desc, email)
}
@Entity
class MemberRole (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val role: ROLE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_member_role_member_id"))
    val member: Member

)

@Entity
class MemberRefreshToken (
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    val member: Member,
    private var refreshToken: String
) {
    @Id
    val memberId: String? = null
    private var reissueCount = 0

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    fun validateRefreshToken(refreshToken: String) = this.refreshToken == refreshToken

    fun increaseReissueCount() {
        reissueCount++
    }
}

