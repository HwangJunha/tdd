package com.around.tdd.service;

import com.around.tdd.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    public boolean checkPassword(String password){
        String regExp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";
        return password.matches(regExp);
    }

    public boolean checkEmail(String email){
        String regExp = "^(?=.{1,30}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regExp);
    }

}
