package com.around.tdd.repository;

import com.around.tdd.vo.RedisAuth;
import org.springframework.data.repository.CrudRepository;

public interface AuthRedisRepository extends CrudRepository<RedisAuth, String> {
}
