package com.jjh.Dlgi.member.repository

import com.jjh.Dlgi.member.entity.Member
import com.jjh.Dlgi.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByLoginId(loginId: String) : Member?
}

interface MemberRoleRepository: JpaRepository<MemberRole, Long>