package org.zerock.springbootb01.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {
  // REST 방식으로 개발시, 로그 기록을 하는 도구들 설정.
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handleBindException(BindException e) {
    log.error(e);
    Map<String, String> errorMap = new HashMap<>();

    if(e.hasErrors()){
      BindingResult bindingResult = e.getBindingResult();

      bindingResult.getFieldErrors().forEach(fieldError -> {
        errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
      });
    }
    return ResponseEntity.badRequest().body(errorMap);
  }

  //실행시점 오류 코드 등 로그 기록하는 설정.
  // 사용할수 없는 bno 로 작성시 발생하는 예외 처리
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handleFkException(Exception e) {
    log.error(e);
    Map<String, String> errorMap = new HashMap<>();

   errorMap.put("time",""+System.currentTimeMillis());
    errorMap.put("msg","constraint fails");
    return ResponseEntity.badRequest().body(errorMap);

  }

  // 데이터 가 존재 하지 않을 경우 예외처리

  // 존재 하지 않은 댓글 번호 삭제시 오류 예외 처리.
  @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handleNoSuchElement(Exception e) {
    log.error(e);
    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time",""+System.currentTimeMillis());
    errorMap.put("msg","No such element Exception");
    return ResponseEntity.badRequest().body(errorMap);

  }

}
