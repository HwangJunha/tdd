package com.around.tdd.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth-property")
@RequiredArgsConstructor
@Getter
public class AuthProperty {
    private final String salt;
}
