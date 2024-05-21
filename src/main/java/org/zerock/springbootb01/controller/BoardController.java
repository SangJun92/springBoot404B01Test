package org.zerock.springbootb01.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.springbootb01.dto.*;
import org.zerock.springbootb01.service.BoardService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor

public class BoardController {

  @Value("${org.zerock.upload.path}")
  private String uploadPath;

  // 게시글에 댓글 갯수 표현하는 목록 출력으로 변경함.
  // 주석으로 내용 변경 확인 테스트
  private final BoardService boardService;
  @GetMapping("/list")
  public void list(PageRequestDTO pageRequestDTO, Model model) {
    // 타입 변경 : BoardListReplyCountDTO, 메서드 명 변경. : 댓글 개수를 저장하는 DTO
//    PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);

    // 타입변경 BoardListAllDTO : 댓글 개수, 이미지 파일의 데이터도 함께 저장하는 DTO
    PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

//    log.info(responseDTO);
    model.addAttribute("responseDTO", responseDTO);
  }
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/register")
  public void registerGET(){}

  @PostMapping("/register")
  public String registerPOST(@Valid BoardDTO boardDTO
                            , BindingResult bindingResult
                            , RedirectAttributes redirectAttributes) {
    log.info("board Post register.......");
    if(bindingResult.hasErrors()) {
      log.info("has errors.......");
      redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
      return "redirect:/board/register";
    }
    log.info(boardDTO);
    Long bno = boardService.register(boardDTO);
    redirectAttributes.addFlashAttribute("result",bno);
    return "redirect:/board/list";
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping({"/read","/modify"})
  public void read(Long bno, PageRequestDTO pageRequestDTO,Model model) {
    BoardDTO boardDTO = boardService.readOne(bno);
    log.info(boardDTO);
    model.addAttribute("dto",boardDTO);
  }
@PreAuthorize("principal.username == #boardDTO.writer")
  @PostMapping("/modify")
  public String modify( PageRequestDTO pageRequestDTO,
                        @Valid BoardDTO boardDTO
                        , BindingResult bindingResult
                        , RedirectAttributes redirectAttributes) {
    log.info("board Modify register.......");
    if(bindingResult.hasErrors()) {
      log.info("has errors.......");
      String link = pageRequestDTO.getLink();
      redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
      redirectAttributes.addAttribute("bno",boardDTO.getBno());
      return "redirect:/board/modify?"+link;
    }
    System.out.println(boardDTO.getBno());
    boardService.modify(boardDTO);
    redirectAttributes.addFlashAttribute("result","modified");
    redirectAttributes.addAttribute("bno",boardDTO.getBno());
    return "redirect:/board/read";
  }

  @PreAuthorize("principal.username == #boardDTO.writer")
  @PostMapping("/remove")
  public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
    Long bno = boardDTO.getBno();
    log.info("remove post......"+bno);
    boardService.remove(bno);

    // 게시물이 데이터베이스상에서 삭제되었으면 첨부파일 삭제
    log.info(boardDTO.getFileNames());
    List<String> fileNames = boardDTO.getFileNames();
    if(fileNames != null && fileNames.size() > 0) {
      removeFiles(fileNames);
    }
    redirectAttributes.addFlashAttribute("result","removed");
    return "redirect:/board/list";
  }

  public void removeFiles(List<String> files) {
    for(String fileName:files){
      Resource resource = new FileSystemResource(uploadPath+ File.separator+fileName);
      String resourceName = resource.getFilename();

      try{
        String contentType = Files.probeContentType(resource.getFile().toPath());
        resource.getFile().delete();

        if(contentType.startsWith("image")) {
          File thumbnailFile = new File(uploadPath+ File.separator+"s_"+fileName);
          thumbnailFile.delete();
        }
      }catch ( Exception e){
        log.error(e.getMessage());
      }
    }// end for
  }
}


















