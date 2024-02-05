package com.jjh.Dlgi.common.status

// Gender 클래스로 남, 여 상태 구분
enum class Gender(val desc: String) {
    MAN("남"),
    WOMAN("여")
}

enum class ResultCode(val msg: String) {
    SUCCESS("정상 처리 되었습니다."),
    ERROR("에러가 발생했습니다.")
}