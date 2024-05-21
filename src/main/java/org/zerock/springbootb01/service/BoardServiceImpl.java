package org.zerock.springbootb01.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.springbootb01.domain.Board;
import org.zerock.springbootb01.dto.*;
import org.zerock.springbootb01.repository.BoardRepository;
import org.zerock.springbootb01.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {
  private final ModelMapper modelMapper;
  private final BoardRepository boardRepository;
  private final ReplyRepository replyRepository;
  private BoardDTO boardDTO;

//    @Override
//  public Long register(BoardDTO boardDTO) {
//    Board board = modelMapper.map(boardDTO, Board.class);
//    Long bno = boardRepository.save(board).getBno();
//    return bno;
//  }

  @Override
  public BoardDTO readOne(Long bno) {
    Optional<Board> result = boardRepository.findById(bno);
    Board board = result.orElseThrow();
//    BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
    BoardDTO boardDTO = entityToDTO(board);
    return boardDTO;
  }

  @Override
  public void modify(BoardDTO boardDTO) {
    Optional<Board> result = boardRepository.findById(boardDTO.getBno());
    Board board = result.orElseThrow();
    board.change(boardDTO.getTitle(), boardDTO.getContent());

    // 이미지 파일 삭제
    board.clearImages();

    // UUID와 파일이름 저장
    if(boardDTO.getFileNames() != null) {
      for (String fileName : boardDTO.getFileNames()) {
        String[] arr = fileName.split("_");
        board.addImage(arr[0], arr[1]);
      }
    }
    boardRepository.save(board);
  }

  @Override
  public void remove(Long bno) {
    replyRepository.deleteByBoard_Bno(bno); // 댓글 달린거 삭제하는 기능
    boardRepository.deleteById(bno);
  }

  @Override
  public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
    //페이지 및 검색 조건 취득
    String[] types = pageRequestDTO.getTypes();
    String keyword = pageRequestDTO.getKeyword();
    Pageable pageable = pageRequestDTO.getPageable("bno");
    //레포지토리를 실행하여 데이터 취득
    Page<Board> result = boardRepository.searchAll(types,keyword,pageable);
    // VO를 DTO로 변환
     List<BoardDTO>dtoList = result.getContent().stream()
        .map(board -> modelMapper.map(board, BoardDTO.class))
        .collect(Collectors.toList());

    return PageResponseDTO.<BoardDTO>withAll()
        .pageRequestDTO(pageRequestDTO)
        .dtoList(dtoList)
        .total((int)result.getTotalElements())
        .build();
  }

  // 게시글 목록 조회시 , 댓글 갯수 같이 출력
  @Override
  public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
    //페이지 및 검색 조건 취득
    String[] types = pageRequestDTO.getTypes();
    String keyword = pageRequestDTO.getKeyword();
    Pageable pageable = pageRequestDTO.getPageable("bno");
    //레포지토리를 실행하여 데이터 취득
    Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types,keyword,pageable);

    // VO를 DTO로 변환, 원래는 엔티티 클래스 타입 -> DTO 타입으로 수동으로 변환.
//    List<BoardDTO>dtoList = result.getContent().stream()
//        .map(board -> modelMapper.map(board, BoardDTO.class))
//        .collect(Collectors.toList());
    // 우리는 자동으로 Projections 이용해서, 미리 자동으로 타입을 변환 하게 설정 해둠.

    return PageResponseDTO.<BoardListReplyCountDTO>withAll()
        .pageRequestDTO(pageRequestDTO)
        .dtoList(result.getContent())
        .total((int)result.getTotalElements())
        .build();
  }

//  @Override
//  public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
//    return null;
//  }
//
  @Override
  public Long register(BoardDTO boardDTO) {
      this.boardDTO = boardDTO;
      Board board = dtoToEntity(boardDTO);
    Long bno = boardRepository.save(board).getBno();
    return bno;
  }

  @Override
  public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
    //페이지 및 검색 조건 취득
    String[] types = pageRequestDTO.getTypes();
    String keyword = pageRequestDTO.getKeyword();
    Pageable pageable = pageRequestDTO.getPageable("bno");
    //레포지토리를 실행하여 데이터 취득
    Page<BoardListAllDTO> result = boardRepository.searchWithAll(types,keyword,pageable);

    return PageResponseDTO.<BoardListAllDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(result.getContent())
            .total((int)result.getTotalElements())
            .build();
  }
}
