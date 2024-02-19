package com.jjh.Dlgi.member.controller

import com.jjh.Dlgi.common.authority.TokenInfo
import com.jjh.Dlgi.common.dto.BaseResponse
import com.jjh.Dlgi.common.dto.CustomUser
import com.jjh.Dlgi.member.dto.LoginDto
import com.jjh.Dlgi.member.dto.MemberDtoRequest
import com.jjh.Dlgi.member.dto.MemberDtoResponse
import com.jjh.Dlgi.member.dto.UpdateDto
import com.jjh.Dlgi.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/member")
@RestController
class MemberController(
    private val memberService: MemberService
){
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit> {
        val resultMsg: String = memberService.signUp(memberDtoRequest)
        // 반환 타입은 BaseResponse<T> 인데 반환시 형식 매개변수인 T 부분에는 데이터 타입을 명시하지 않아도 되므로 빈 자료형이라는 Unit 타입을 명시
        // 예외가 발생하면 CustomExceptionHandler 에 있는 예외처리 메소드를 호출하게 되고
        // 예외가 발생하지 않고 성공적으로 회원가입 로직이 완료되면 BaseResponse(message = resultMsg) 를 반환한다.
        // BaseResponse 에 message 만 넘겨준 이유는 BaseResponse 의 생성자를 성공했을때 코드와 메세지는 이미 초기화 하였지만
        // signUp 을 성공적으로 완료시 "회원가입이 성공적으로 완료되었습니다." 라는 메세지를 반환하가 때문에 message 만 넘겨준다.
        // 굳이 메세지를 넘겨주지 않는다면 return BaseResponse() 로 코드를 수정해도 무방하다.
        return BaseResponse(message = resultMsg)
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): BaseResponse<Map<String, String>> {
        val tokenInfo = memberService.login(loginDto)
        return BaseResponse(data = tokenInfo)
    }

    /**
     * 내 정보 보기
     */
    @GetMapping("/info")
    fun searchMyInfo(): BaseResponse<MemberDtoResponse> {
        // SecurityContextHolder.getContext() 안에 들어있는 CustomUser 정보에서 UserId 를 꺼내서 조회
        val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).userId
        val response = memberService.searchMyInfo(userId)
        return BaseResponse(data = response)
    }

    /**
     * 내 정보 수정
     */
    @PutMapping("/info")
    fun saveMyInfo(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit> {
        val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).userId
        memberDtoRequest.id = userId
        val resultMsg: String = memberService.saveMyInfo(memberDtoRequest)
        return BaseResponse(message = resultMsg)
    }

    /**
     * 쿼리를 이용한 정보 수정
     */
    @PutMapping("/update")
    fun updateMyName(@RequestBody @Valid updateDto: UpdateDto): BaseResponse<MemberDtoResponse> {
        val resultMsg: String = memberService.updateUserInfo(updateDto)
        val response = memberService.searchMyInfo(updateDto.id.toString().toLong())
        return BaseResponse(data = response, message = resultMsg)
    }
}


// @Valid : memberDtoRequest 에 설정한 validation을 사용한다는 어노테이션