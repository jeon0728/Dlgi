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
            "SET " +
            "name = :name, " +
            "birth_date = :birthDate, " +
            "gender = :gender, " +
            "email = :email " +
            "WHERE id = :id", nativeQuery = true)
    fun updateUserInfo(name: String, birthDate: String, gender: String, email:String, id: Long): Int
}

interface MemberRoleRepository: JpaRepository<MemberRole, Long>