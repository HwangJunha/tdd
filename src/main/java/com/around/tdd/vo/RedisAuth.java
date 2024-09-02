package com.around.tdd.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.util.Objects;

@Getter
@RedisHash(value = "auth", timeToLive = 1800)
@AllArgsConstructor
public class RedisAuth {
    private String id;
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedisAuth that = (RedisAuth) o;
        return Objects.equals(id, that.id) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}
