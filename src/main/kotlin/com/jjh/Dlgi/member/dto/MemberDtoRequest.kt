package com.jjh.Dlgi.member.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.jjh.Dlgi.common.annotaion.ValidEnum
import com.jjh.Dlgi.common.status.Gender
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MemberDtoRequest (
    val id: Long?,

    @field:NotBlank //빈값 허용하지 않는 어노테이션
    @JsonProperty("loginId") //json 요청 값과 매핑
    private val _loginId: String?,

    @field:NotBlank
    @JsonProperty("password")
    @field:Pattern( //정규식 패턴 입력
        regexp="^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요"
    )
    private val _password: String?,

    @field:NotBlank
    @JsonProperty("name")
    private val _name: String?,

    @field:NotBlank
    @JsonProperty("birthDate")
    @field:Pattern( //정규식 패턴 입력
        regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
        message = "날짜형식(YYYY-MM-DD)을 확인해주세요"
    )
    private val _birthDate: String?,

    @field:NotBlank
    @JsonProperty("gender")
    @field:ValidEnum(enumClass = Gender::class, message = "제대로 입력해라 으이? (MAN or WOMAN)")
    private val _gender: String?,

    @field:NotBlank
    @JsonProperty("email")
    @field:Email
    private val _email: String?,
) {

    val loginId: String
        get() = _loginId!! //null로 받았지만 get의 반환타입은 null을 허용하지 않기 때문에 에러남 '!!' 를 써서 null 허용이 되지 않는값으로 반환해줘야함
    val password: String
        get() = _password!!
    val name: String
        get() = _name!!
    val birthDate: LocalDate
        get() = _birthDate!!.toLocalDate() //확장함수를 사용하여 원하는 포맷의 LocalDate 타입으로 형변환
    val gender: Gender
        get() = Gender.valueOf(_gender!!) //Gender라는 enum class 타입으로 형변환
    val email: String
        get() = _email!!

    private fun String.toLocalDate(): LocalDate =
        LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}