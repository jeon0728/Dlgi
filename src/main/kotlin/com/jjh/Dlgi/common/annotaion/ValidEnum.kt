package com.jjh.Dlgi.common.annotaion

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD) //해당 Annotation 이 어떤 요소에 붙일 수 있는지 명시
@Retention(AnnotationRetention.RUNTIME) //어노테이션을 컴파일된 클래스 파일에 저장할 것인지 런타임에 반영할 것인지 정의
@MustBeDocumented //API의 일부분으로 문서화하기 위해 사용.
@Constraint(validatedBy = [ValidEnumValidator::class]) //[] 안에 작성한 validator 넣어주기
annotation class ValidEnum(
    val message: String = "Invalid enum value",
    // groups, payload는 default 값
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val enumClass: KClass<out Enum<*>>
)

class ValidEnumValidator : ConstraintValidator<ValidEnum, Any> {
    private lateinit var enumValues: Array<out Enum<*>>
    //control + o 누르면 자동으로 오버라이드 함수 목록 뜸
    override fun initialize(annotation: ValidEnum) {
        enumValues = annotation.enumClass.java.enumConstants
    }
    override fun isValid(
        value: Any?,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value == null) {
            return true
        }
        return enumValues.any { it.name == value.toString() }
    }
}