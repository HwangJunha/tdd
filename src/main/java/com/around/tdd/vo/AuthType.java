package com.around.tdd.vo;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AuthType {
    AUTH_NUMBER("auth-member"),
    FIND_MEMBER("find-member"),
    NONE("none");

    private final String redisKey;

    public static final Map<AuthType, String> AUTH_TYPE_MAP =
            Collections.unmodifiableMap(Stream.of(values()).collect(Collectors.toMap(e -> e, AuthType::getRedisKey)));

    AuthType(String redisKey) {
        this.redisKey = redisKey;
    }

    public String getRedisKey() {
        return redisKey;
    }

}
