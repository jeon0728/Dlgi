package com.jjh.Dlgi.member.repository

import com.jjh.Dlgi.member.entity.Member
import com.jjh.Dlgi.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByLoginId(loginId: String): Member?

    @Modifying
    @Query("UPDATE Member " +
            "SET name = :name " +
            "WHERE id = :id")
    fun updateUserName(name: String, id: Long): Int
}

interface MemberRoleRepository: JpaRepository<MemberRole, Long>