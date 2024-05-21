package org.zerock.springbootb01.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.springbootb01.domain.Member;
import org.zerock.springbootb01.dto.MemberJoinDTO;
import org.zerock.springbootb01.service.MemberService;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;



    @GetMapping("/login")
    public void loginGet(String error, String logout) {

        log.info("login get....");
        log.info("logout: " + logout);
        if(logout != null){
            log.info("user logout........");
        }
    }

//    @PostMapping("/login")
//    public String login(Model model,
//                        HttpServletRequest req,
//                        String mid,
//                        String mpw,
//                        RedirectAttributes redirectAttributes) {
//        try{
//            Member loginInfo = memberService.login(mid,mpw);
//            HttpSession session = req.getSession(true);
//            session.setAttribute("loginInfo", loginInfo);
//            model.addAttribute("info",loginInfo);
//            return "redirect:/board/list";
//        }catch(Exception e){
//            redirectAttributes.addFlashAttribute("error","아이디와 비밀번호를 확인해주세요.");
//            return "redirect:/member/login";
//        }
//        }

    @GetMapping("/join")
    public void joinGET(){
        log.info("join get....");
    }

    @PostMapping("/join")
    public String joinPOST(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes){

        log.info("join get....");
        log.info(memberJoinDTO);

        try{
            // 회원가입 서비스 실행
            memberService.join(memberJoinDTO);

            // 아이디가 존재 할 경우 에러 발생
        }catch (MemberService.MidExistException e){

            // 에러 발생시 리다이렉트 페이지에 error = mid값을 가지고 이동
            redirectAttributes.addFlashAttribute("error", "mid");
            return "redirect:/member/login";
        }

        redirectAttributes.addFlashAttribute("result","success");
        return "redirect:/member/login"; // 회원가입 후 로그인
    }

//    @GetMapping("/modify")
//    public void modifyGET() {
//        log.info("modify GET....");
//        model.addAttribute("mid", mid);
//    }
//
//    @PostMapping("/modify")
//    public String modifyPOST() {
//        log.info("modify POST.......");
//
//        MemberService.modify(mpw, mid);
//        return "redirect:/board/list";
//    }

    @GetMapping("/modify")
    public void modifyGet(String mid, String mpw, Model model){
        log.info("modify get......");
        model.addAttribute("mid","mpw");
    }
    @PostMapping("/modify")
    public String modifyPost(String mid, String mpw){
        log.info("modify Post......");
        memberService.modify(mpw, mid);
        return "redirect:/board/list";
    }

//    // 탈퇴하기
//    @GetMapping("/exit")
//    public String exit() {
//        User user = (User) session.getAttribute("user_info");
//        userRepository.delete(user);
//        session.invalidate();
//
//        return "redirect:/";
//    }

    // 탈퇴
//    @RequestMapping("/out")
//    public String out(HttpSession session) {
//        String id = (String) session.getAttribute("loginId");
//        service.delMember(id);
//        session.invalidate();
//        //return "redirect:/member/logout";
//        return "index";
//    }

}