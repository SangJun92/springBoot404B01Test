package org.zerock.springbootb01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.springbootb01.domain.Member;
import org.zerock.springbootb01.domain.MemberRole;
import org.zerock.springbootb01.dto.MemberJoinDTO;
import org.zerock.springbootb01.repository.MemberRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void modify(MemberJoinDTO memberJoinDTO) {
        String mid = memberJoinDTO.getMid();
        String encodedPassword = passwordEncoder.encode(memberJoinDTO.getMpw());
        String name = memberJoinDTO.getName();
        String email = memberJoinDTO.getEmail();
        String address = memberJoinDTO.getAddress();

        memberRepository.updateMember(mid, encodedPassword, name, email, address);
    }

    @Override
    public void remove(String mid) {
        memberRepository.deleteById(mid);
    }

//    @Override
//    public boolean checker(MemberJoinDTO memberJoinDTO) {
//        String mid = memberJoinDTO.getMid();
//        boolean exist = memberRepository.existsById(mid);
//        return exist;
//    }

    @Override
    public int duplicate(String mid) {
        return memberRepository.duplicate(mid);
    }

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {

        // 화면에서 가지고온 ID를 저장
        String mid = memberJoinDTO.getMid();

        // JPA에 지원하는 ID존재 여부 확인 메서드 실행
        boolean exist = memberRepository.existsById(mid);

        // ID가 이미 존재하면 에러를 발생시키는 if문
        if (exist) {
            throw new MidExistException();
        }

        // ID가 존재하지 않으면 Member객체로 변환
        Member member = modelMapper.map(memberJoinDTO, Member.class);
        // 비밀번호 암호화
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
        // 권한 설정
        member.addRole(MemberRole.USER);

        log.info("===========================");
        log.info(member);
        log.info(member.getRoleSet());

        // 데이터베이스에 저장
        memberRepository.save(member);
    }
}
