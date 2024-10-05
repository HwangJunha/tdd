package com.around.tdd.service;

import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.Member;
import com.around.tdd.vo.MemberInfo;
import com.around.tdd.vo.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 사용자 입력을 한 번에 체크하고 적절한 메시지를 반환
     * @param id - 검사할 ID
     * @param email - 검사할 email
     * @param password - 검사할 패스워드
     * @return 적절한 메시지
     */
    public ValidationResult checkUserInput(String id, String email, String password){
        if(!checkId(id)){
            return new ValidationResult(false, "이미 존재하는 아이디입니다.");
        }
        if(!checkEmail(email)){
            return new ValidationResult(false, "유효하지 않은 이메일 형식입니다.");
        }
        if(!checkPassword(password)){
            return new ValidationResult(false, "패스워드는 8자 이상 16자 이하이며, 문자, 숫자, 특수문자를 포함해야 합니다.");
        }
        return new ValidationResult(true, "모든 입력이 유효합니다.");
    }

    /**
     * 패스워드 체크
     * @param password - 패스워드
     * @return 유효한지 boolean으로 체크
     */
    public boolean checkPassword(String password){
        String regExp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";
        return password.matches(regExp);
    }

    /**
     * 이메일 체크
     * @param email - 이메일
     * @return 유효한지 boolean으로 체크
     */
    public boolean checkEmail(String email){
        String regExp = "^(?=.{1,30}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regExp);
    }

    /**
     * 같은 아이디가 있는지 체크
     * @param id - 검사할 ID
     * @return
     */
    public boolean checkId(String id){
        return memberRepository.countById(id).intValue() > 0 ? false : true;
    }

    /**
     * 사용자 입력
     * @param member - 입력될 사용자
     * @return 입력된 값 반환
     */
    @Transactional
    public Member insertMemberInfo(Member member){
        return memberRepository.save(member);
    }

    /**
     * 사용자 정보를 조회한다
     * @param memberSeq - 사용자 번호
     * @return 사용자 정보를 반환한다
     */
    public Optional<Member> memberFindById(Long memberSeq){
        return memberRepository.findById(memberSeq);
    }

    /**
     * 조회 후 패스워드를 변경한다
     * @param memberSeq - 사용자 번호
     * @param newPassword - 새로운 패스워드
     * @return - true: 패스워드 변경 완료, false: 패스워드 변경 실패
     */
    @Transactional
    public boolean changePassword(Long memberSeq, String newPassword){
        var savedMember = memberRepository.findById(memberSeq);
        if(savedMember.isEmpty()){
            return false;
        }
        savedMember.get().setPassword(newPassword);
        return true;
    }


    /**
     * 사용자 정보 변경
     * @param memberSeq - 사용자 번호
     * @param name - 이름
     * @param phone - 번호
     * @param email - 이메일
     * @param nick - 닉네임
     * @param address - 주소
     * @param detailAddress - 상세 주소
     * @param post - 우편번호
     * @return 수정된 회원정보
     */
    @Transactional
    public MemberInfo changeMemberInfo(Long memberSeq,String name, String phone, String email, String nick, String address, String detailAddress, String post){
        var savedMember = memberRepository.findById(memberSeq);
        if(savedMember.isEmpty()){
            return null;
        }
        if(name != null && !name.isBlank())
            savedMember.get().getMemberInfo().setName(name);
        if(phone != null && !phone.isEmpty())
            savedMember.get().getMemberInfo().setPhone(phone);
        if(email != null && !email.isEmpty())
            savedMember.get().getMemberInfo().setEmail(email);
        if(nick != null && !nick.isEmpty())
            savedMember.get().getMemberInfo().setNick(nick);
        if(address != null && !address.isEmpty())
            savedMember.get().getMemberInfo().setAddress(address);
        if(detailAddress != null && !detailAddress.isEmpty())
            savedMember.get().getMemberInfo().setDetailAddress(detailAddress);
        if(post != null && !post.isEmpty())
            savedMember.get().getMemberInfo().setPost(post);
        return savedMember.get().getMemberInfo();
    }
}
