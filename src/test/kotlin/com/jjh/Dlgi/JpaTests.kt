package com.jjh.Dlgi

import com.jjh.Dlgi.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class JpaTests (
    val entityManager: TestEntityManager, // TestEntityManager 는 데이터를 임시로 저장하는 방법
    val memberRepository: MemberRepository
) {
    @Test
    fun `Jpa Update Test` () {
        //val member = Member("", "", "")
    }
}