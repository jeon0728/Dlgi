package com.jjh.Dlgi.member.repository

import com.jjh.Dlgi.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByLoginId(loginId: String) : Member?
}