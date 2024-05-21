package org.zerock.springbootb01.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
  private Long bno;
  @NotEmpty
  @Size(min=3, max = 100)
  private String title;
  @NotEmpty
  private String content;
  @NotEmpty
  private String writer;

  // 첨부파일의 이름들
  private List<String> fileNames;

  private LocalDateTime regDate;
  private LocalDateTime modDate;

}
















