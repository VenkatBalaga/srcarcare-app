package com.srcarcare.app.repository;

import com.srcarcare.app.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByMerchantTransactionId(String merchantTransactionId);
}
