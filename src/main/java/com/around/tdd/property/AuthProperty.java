package com.around.tdd.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth-property")
@Getter
@Setter
public class AuthProperty {
    private String salt;
}
