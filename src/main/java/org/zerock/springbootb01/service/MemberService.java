package org.zerock.springbootb01.service;

import org.zerock.springbootb01.domain.Member;
import org.zerock.springbootb01.dto.MemberJoinDTO;

public interface MemberService {


    void modify(MemberJoinDTO memberJoinDTO);

    void remove(String mid);

//    int duplicate(String mid);

    boolean checker(MemberJoinDTO memberJoinDTO);


    static class MidExistException extends Exception {

    }

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;


}


