package org.zerock.springbootb01.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.springbootb01.dto.PageRequestDTO;
import org.zerock.springbootb01.dto.PageResponseDTO;
import org.zerock.springbootb01.dto.ReplyDTO;
import org.zerock.springbootb01.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
@Log4j2
public class ReplyController {

  private final ReplyService replyService;

  @Tag(name = "Replies POST", description = "POST 방식으로 댓글 등록")
  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  // ResponseEntity, 데이터를 HTTP 상태 코드와 같이 , 앞단에 전달하기.
  // JSON  타입의 문자열로 전달시 사용하고,
  // 서버에 도착하면 JSON -> ReplyDTO 타입으로 자동으로 변환,
  // Jackson 라이브러리가, 컨버터 역할. 기본 탑재,
  // 요청시, ReplyDTO 의 타입으로, application/json 마임 타입으로 전달 해야함.
  public Map<String, Long> register(@Valid @RequestBody ReplyDTO replyDTO, BindingResult bindingResult) throws BindException {

    log.info(replyDTO);

    // 더미 데이터 넣기 1
    // Map<String, Long> resultMap = Map.of("rno",123L);
    // 오류 및 , 유효성 체크등 오류 발생시, 에러 표시.
    if (bindingResult.hasErrors()) {
      throw new BindException(bindingResult);
    }

    // 더미데이터 넣기 2
    Map<String, Long> resultMap = new HashMap<>();
    //resultMap.put("rno",1234L);
    //실제 데이터 넣는 서비스 추가하기.
    Long rno = replyService.register(replyDTO);
    resultMap.put("rno", rno);


    return resultMap;
  }

  @Tag(name = "Replies of Board", description = "get 방식특정 게시물의 댓글 목록")
  // 특정 댓글 목록 조회
  @GetMapping(value = "/list/{bno}")
  public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno, PageRequestDTO pageRequestDTO) {
     PageResponseDTO<ReplyDTO> responseDTO= replyService.getListOfBoard(bno, pageRequestDTO);
    return responseDTO;
  }

  @Tag(name = "댓글 읽기", description = "get 방식 특정 게시물의 댓글 조회")
  // 특정 댓글 목록 조회
  @GetMapping(value = "/{rno}")
  public ReplyDTO getReplyDto(@PathVariable("rno") Long rno) {
    ReplyDTO replyDTO= replyService.read(rno);
    return replyDTO;
  }

  @Tag(name = "댓글 삭제", description = "DELETE 방식 특정 게시물의 댓글 삭제")
  // 특정 댓글 목록 조회
  @DeleteMapping(value = "/{rno}")
  public Map<String,Long> remove(@PathVariable("rno") Long rno) {
    replyService.remove(rno);
    Map<String,Long> resultMap = new HashMap<>();
    resultMap.put("rno", rno);
    return resultMap;
  }

  @Tag(name = "댓글 수정", description = "PUT 방식 특정 게시물의 댓글 수정")
  // 특정 댓글 목록 조회
  @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String,Long> modify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO) {
    replyDTO.setRno(rno);
    replyService.modify(replyDTO);
    Map<String,Long> resultMap = new HashMap<>();
    resultMap.put("rno", rno);
    return resultMap;
  }


}
