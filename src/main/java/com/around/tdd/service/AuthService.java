package com.around.tdd.service;

import com.around.tdd.repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    public AuthService(
            AuthRepository authRepository
    ){
        this.authRepository = authRepository;
    }
}
