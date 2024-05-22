package org.zerock.springbootb01.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.springbootb01.dto.MemberJoinDTO;
import org.zerock.springbootb01.service.MemberService;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MemberJSONController {
    private final MemberService memberService;

    @GetMapping("/duplicate")
    public int duplicate(String mid) {
        log.info("remove post……"+mid);

        return memberService.duplicate(mid);
    }
}
