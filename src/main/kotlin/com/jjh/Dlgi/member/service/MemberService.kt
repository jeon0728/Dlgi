package com.jjh.Dlgi.member.service

import com.jjh.Dlgi.common.authority.JwtTokenProvider
import com.jjh.Dlgi.common.authority.TokenInfo
import com.jjh.Dlgi.common.exception.InvalidInputException
import com.jjh.Dlgi.common.status.ROLE
import com.jjh.Dlgi.member.dto.LoginDto
import com.jjh.Dlgi.member.dto.MemberDtoRequest
import com.jjh.Dlgi.member.dto.MemberDtoResponse
import com.jjh.Dlgi.member.entity.Member
import com.jjh.Dlgi.member.entity.MemberRole
import com.jjh.Dlgi.member.repository.MemberRepository
import com.jjh.Dlgi.member.repository.MemberRoleRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        // ID 중복 검사
        var member: Member? = memberRepository
            .findByLoginId(memberDtoRequest.loginId)
        if (member != null) {
            throw InvalidInputException("loginId", "이미 등록된 ID 입니다.")
        }
        member = memberDtoRequest.toEntity()
        memberRepository.save(member)

        val memberRole: MemberRole = MemberRole(null, ROLE.JUNHO, member)
        memberRoleRepository.save(memberRole)
        return "회원가입이 완료 되었습니다."
    }

    /**
     * 로그인
     */
    fun login(loginDto: LoginDto): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.loginId, loginDto.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        return jwtTokenProvider.createToken(authentication)
    }

    fun searchMyInfo(id: Long): MemberDtoResponse {
        val member: Member = memberRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "회원번호(${id}가 존재하지 않는 유저입니다.)")
        return member.toDto()
    }
}