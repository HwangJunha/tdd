package com.around.tdd.repository;

import com.around.tdd.vo.Seller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SellerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SellerRepository sellerRepository;

    // 예제 테스트 케이스
    @Test
    void whenFindById_thenReturnSeller() {
        Seller seller = Seller
                .builder()
                .phone("01084282511")
                .post("123456")
                .address("신림")
                .name("testSeller")
                .detailAddress("아덴빌")
                .salesTotalAmount(0)
                .deliveryTotalAmount(0)
                .build();
        entityManager.persistAndFlush(seller);

        Optional<Seller> found = sellerRepository.findById(seller.getSellerSeq());
        assertThat(found).isPresent()
                .hasValueSatisfying(s -> assertThat(s.getName()).isEqualTo("testSeller"));
    }
}