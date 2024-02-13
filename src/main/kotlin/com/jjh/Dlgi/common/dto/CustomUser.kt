package com.jjh.Dlgi.common.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

// User 클래스는 userName, password, authorities 만을 인자로 가지고 있다.
// 우리는 userId 라는 값을 추가로 사용하고 싶었기 때문에 User 클래스를 상속받은 CustomUser 를 만들어준다.
class CustomUser (
    val userId: Long, // 추가 할 속성
    userName: String, // 기존 속성 1
    password: String, // 기존 속성 2
    authorities: Collection<GrantedAuthority> // 기존 속성 3
) : User(userName, password, authorities)