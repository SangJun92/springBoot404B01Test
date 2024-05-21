package org.zerock.springbootb01.service;

import org.zerock.springbootb01.domain.Member;
import org.zerock.springbootb01.dto.MemberJoinDTO;

public interface MemberService {


    void modify(String mpw, String mid);

    static class MidExistException extends Exception {

    }

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
}
