package com.around.tdd.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@DataJpaTest
class MemberRepositoryTest {



}