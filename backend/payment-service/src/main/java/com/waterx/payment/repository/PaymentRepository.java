package com.waterx.payment.repository;

import com.waterx.payment.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentRecord, UUID> {
    Optional<PaymentRecord> findByRazorpayOrderId(String razorpayOrderId);
}
