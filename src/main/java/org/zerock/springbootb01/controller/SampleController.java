package org.zerock.springbootb01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor

public class SampleController {
    @GetMapping("/")
    public String index() {
        return "redirect:/board/list";
    }
}
