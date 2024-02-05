package com.jjh.Dlgi.common.exception

import com.jjh.Dlgi.common.dto.BaseResponse
import com.jjh.Dlgi.common.status.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class) // Controller 에 선언한 @Valid 에서 발생하는 예외(MethodArgumentNotValidException)를 받아준다.
    protected fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()

        // 아래 코드는 map 형태인 errors를 만드는 과정
        // MethodArgumentNotValidException 에서 발생한 모든 에러들을 foreach 문으로 errors 라는 map 에 저장
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field // error 를 FieldError 로 타입 캐스팅
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }

        // 반환 타입은 ResponseEntity(BaseResponse<T>, HttpStatus.BAD_REQUEST)
        // BaseResponse 는 제네릭 타입의 인자를 받기 떄문에 어떠한 데이터 타입도 받을 수 있다.
        // 그래서 Map<String, String> 형태의 인자를 넘겨주고, 이 map 은 BaseResponse 클래스에 data<T>에 매핑됨
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, errors, ResultCode.ERROR.msg), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidInputException::class) // 우리가 만든 예외(InvalidInputException)가 발생하면 받아준다.
    protected fun invalidInputException(ex: InvalidInputException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, errors, ResultCode.ERROR.msg), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class) // MethodArgumentNotValidException, InvalidInputException 이외의 예외가 발생하면 받아준다.
    protected fun defaultException(ex: Exception): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(" " to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, errors, ResultCode.ERROR.msg), HttpStatus.BAD_REQUEST)
    }

}