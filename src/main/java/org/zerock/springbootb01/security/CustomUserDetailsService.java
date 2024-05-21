package org.zerock.springbootb01.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.springbootb01.domain.Member;
import org.zerock.springbootb01.repository.MemberRepository;
import org.zerock.springbootb01.security.dto.MemberSecurityDTO;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

//    private PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

//    public CustomUserDetailsService() {
//        passwordEncoder = new BCryptPasswordEncoder();
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에 username으로 검색한 회원 정보를 취득
        log.info("loadUserByUsername: " + username);

        Optional<Member> result = memberRepository.getWithRoles(username);

        if (result.isEmpty()) { // 해당 아이디를 가진 사용자가 없다면
            throw new UsernameNotFoundException("username not found...");
        }
        // 데이터가 있으면 멤버의 데이터를 저장
        Member member = result.get();

        // Member객체를 MemberSecurityDTO객체로 변환
        MemberSecurityDTO memberSecurityDTO =
                new MemberSecurityDTO(
                        member.getMid(),
                        member.getMpw(),
                        member.getEmail(),
                        member.isDel(),
                        false,
                        member.getRoleSet()
                                .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name()))
                                .collect(Collectors.toList())
                );

        log.info("memberSecurityDTO");
        log.info(memberSecurityDTO);

        return memberSecurityDTO;

//        UserDetails userDetails = User.builder()
//                .username(username)
//                .password(passwordEncoder.encode("1111"))
//                .authorities("ROLE_USER")
//                .build();
//
//        return userDetails;
    }
}
