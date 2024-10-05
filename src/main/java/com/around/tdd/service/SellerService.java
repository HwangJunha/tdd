package com.around.tdd.service;

import com.around.tdd.repository.SellerRepository;
import com.around.tdd.vo.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    @Transactional
    public Seller createSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

}
