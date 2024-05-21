package org.zerock.springbootb01.dto;

import lombok.Data;

@Data
public class MemberJoinDTO {

    private String mid;
    private String mpw;
    private String name;
    private String email;
    private String address;
    private String del;
    private String social;
}
