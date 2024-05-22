package org.zerock.springbootb01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.springbootb01.domain.Reply;
import org.zerock.springbootb01.dto.PageRequestDTO;
import org.zerock.springbootb01.dto.PageResponseDTO;
import org.zerock.springbootb01.dto.ReplyDTO;
import org.zerock.springbootb01.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService {
  // 의존성 주입, DI
  private final ReplyRepository replyRepository;
  private final ModelMapper modelMapper;

  // 댓글 등록
  @Override
  public Long register(ReplyDTO replyDTO) {
    // replyDTO -> 엔티티 클래스 타입으로 변환,
    Reply reply = modelMapper.map(replyDTO, Reply.class);
    Long rno = replyRepository.save(reply).getRno();

    return rno;
  }

  // 댓글 읽기
  @Override
  public ReplyDTO read(Long rno) {
    // 댓글 번호로 찾기 했고, 반환 타입은 옵션널
    Optional<Reply> replyOptional = replyRepository.findById(rno);
    // 옵션널 타입 -> 엔티티 클래스 타입으로 변환,
    Reply reply = replyOptional.orElseThrow();
    // 엔티티 클래스 타입 -> DTO 데이터 전달용으로 사용하는 타입으로 변환하기.
    return modelMapper.map(reply, ReplyDTO.class);
  }


  // 댓글 수정
  @Override
  public void modify(ReplyDTO replyDTO) {
// 댓글 번호로 찾기 했고, 반환 타입은 옵션널
    Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());
    // 옵션널 타입 -> 엔티티 클래스 타입으로 변환,
    Reply reply = replyOptional.orElseThrow();
    // 댓글 내용 수정하는 메서드
    reply.changeText(replyDTO.getReplyText());
    // 엔티티 클래스 타입 -> DTO 데이터 전달용으로 사용하는 타입으로 변환하기.
    replyRepository.save(reply);
  }

  // 댓글 삭제
  @Override
  public void remove(Long rno) {
    replyRepository.deleteById(rno);
  }

  @Override
  public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
    //PageRequestDTO : 화면에서 백엔드로 보내는 페이징 정보가 들어 있다. 현재 페이지, 크기 등.
    // 페이징 하기위한 준비물 정보들.
    Pageable pageable = (Pageable) PageRequest.of(pageRequestDTO.getPage() <= 0 ? 0: pageRequestDTO.getPage()-1,pageRequestDTO.getSize(), Sort.by("rno").ascending());
    // 페이징 처리가 된 댓글 목로들 조회.
    Page<Reply> result = replyRepository.listOfBoard(bno,pageable);
    // 스트림 병렬 처리해서, 여러개 댓글의 내용을, 타입을 변환하기, 엔티티 -> DTO 타입으로 변환. -> 다시 리스트로
    List<ReplyDTO> dtoList = result.getContent().stream().map(reply -> modelMapper.map(reply,ReplyDTO.class)).collect(Collectors.toList());
    // 서버에서, 페이징 처리하고, 화면으로 전달 해주기.
    // PageResponseDTO 로 응답해주기.
    return PageResponseDTO.<ReplyDTO>withAll()
        .pageRequestDTO(pageRequestDTO)
        .dtoList(dtoList)
        .total((int)result.getTotalElements())
        .build();
  }
}
