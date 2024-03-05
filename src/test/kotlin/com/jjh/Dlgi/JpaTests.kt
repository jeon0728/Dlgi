package com.jjh.Dlgi

import com.jjh.Dlgi.board.entity.Board
import com.jjh.Dlgi.member.repository.MemberRepository
import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.QueryFactoryImpl
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.singleQuery
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class JpaTests (
    val entityManager: TestEntityManager, // TestEntityManager 는 데이터를 임시로 저장하는 방법
    val memberRepository: MemberRepository,
    val queryFactory: QueryFactoryImpl,
    val springDataQueryFactory: SpringDataQueryFactory
) {
    @Test
    fun `Jdsl Test` () {
        queryFactory.singleQuery<Board> {
            select(entity(Board::class))
            from(entity(Board::class))
            where(col(Board::seq).equal(1))
        }
    }
}