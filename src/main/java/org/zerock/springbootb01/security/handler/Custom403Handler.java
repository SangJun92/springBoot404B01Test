package org.zerock.springbootb01.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.info("--------------ACCESS DENIED-----------------");

        // 돌려줄 상태를 403으로 설정
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // JSON 요청이었는지 확인
        // ContentType을 변수에 저장
        String contentType = request.getHeader("Content-Type");

        // ContentType의 종류가 json이면 true
        boolean jsonRequest = contentType.startsWith("application/json");

        log.info("isJSON: " + jsonRequest);

        // 일반 request
        // json 타입이 아닐 경우 login 페이지로 이동
        if (!jsonRequest) {
            response.sendRedirect("/member/login?error=ACCESS_DENIED");
        }
    }
}
