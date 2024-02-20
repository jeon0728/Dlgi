package com.jjh.Dlgi.member.service

import com.jjh.Dlgi.common.authority.JwtTokenProvider
import com.jjh.Dlgi.common.authority.TokenInfo
import com.jjh.Dlgi.common.exception.InvalidInputException
import com.jjh.Dlgi.common.status.ROLE
import com.jjh.Dlgi.member.dto.LoginDto
import com.jjh.Dlgi.member.dto.MemberDtoRequest
import com.jjh.Dlgi.member.dto.MemberDtoResponse
import com.jjh.Dlgi.member.dto.UpdateDto
import com.jjh.Dlgi.member.entity.Member
import com.jjh.Dlgi.member.entity.MemberRefreshToken
import com.jjh.Dlgi.member.entity.MemberRole
import com.jjh.Dlgi.member.repository.MemberRefreshTokenRepository
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
    private val memberRefreshTokenRepository: MemberRefreshTokenRepository,
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

        // insert 시에는 save를 사용해야함
        // 새로운 데이터를 저장 할때는 최초 엔티티를 초기화 해야하기 때문에
        member = memberDtoRequest.toEntity()
        memberRepository.save(member)
        memberRepository.flush()

        val memberRole: MemberRole = MemberRole(null, ROLE.JUNHO, member)
        memberRoleRepository.save(memberRole)
        return "회원가입이 완료 되었습니다."
    }

    /**
     * 로그인
     */
    fun login(loginDto: LoginDto): Map<String, String> {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.loginId, loginDto.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        val tokenInfo = jwtTokenProvider.createToken(authentication)
        val member = memberRepository.findByLoginId(loginDto.loginId)?: throw InvalidInputException("id", "로그인 id(${loginDto.loginId}가 존재하지 않는 유저입니다.)")
        val memberRefreshToken = memberRefreshTokenRepository.findByIdOrNull(loginDto.loginId)

        memberRefreshToken?.updateRefreshToken(tokenInfo.refreshToken) ?: memberRefreshTokenRepository.save(MemberRefreshToken(member!!, tokenInfo.refreshToken))

        val tokenInfoMap = mapOf<String, String>("grantType" to tokenInfo.grantType, "accessToekn" to tokenInfo.acceesToken, "refreshToken" to tokenInfo.refreshToken)
        return tokenInfoMap
    }

    /**
     * 정보 조회
     */
    fun searchMyInfo(loginId: String): MemberDtoResponse {
        val member: Member = memberRepository.findByLoginId(loginId) ?: throw InvalidInputException("id", "회원번호(${loginId}가 존재하지 않는 유저입니다.)")
        return member.toDto()
    }

    /**
     * 정보 수정
     */
    fun saveMyInfo(memberDtoRequest: MemberDtoRequest): String {
        // 아래 코드와 같이 toEntity 메소드를 사용할 경우
        // fun toEntity() : Member = Member(id, loginId, password, name, birthDate, gender, email)
        // 엔티티에 새로운 값만 할당해주기만 하기 때문에 변경감지가 일어나지 않는다.
        val member: Member = memberDtoRequest.toEntity()
        memberRepository.save(member)

        // 아래 코드와 같이 엔티티의 특정 필드에 set 을 해주게 되면 변경감지가 일어난다.
        // 그래서 따로 save를 해주지 않더라도 update 가 가능해진다.
        //val member: Member = memberRepository.findByIdOrNull(memberDtoRequest.id.toString().toLong()) ?: throw InvalidInputException("id", "회원번호(${memberDtoRequest.id}가 존재하지 않는 유저입니다.)")
        //member.name = memberDtoRequest.name
        return "수정이 완료되었습니다."
    }

    /**
     * 정보 수정 (native query)
     */
    fun updateUserInfo(updateDto: UpdateDto): String {
        //val member: Member = memberDtoRequest.toEntity()
        val resultRow = memberRepository.updateUserInfo(updateDto.name, updateDto.birthDate.toString(), updateDto.gender.name, updateDto.email, (updateDto.id).toString().toLong())
        var resultMsg = "수정이 완료되었습니다."
        if (resultRow < 0) resultMsg = "수정이 완료되지 않았습니다."
        return resultMsg
    }
}