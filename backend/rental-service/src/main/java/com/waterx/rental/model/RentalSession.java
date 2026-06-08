package com.waterx.rental.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rental_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "station_id", nullable = false)
    private String stationId;

    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_name")
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalStatus status = RentalStatus.PENDING;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "bajie_trade_no")
    private String bajieTradeNo;

    @Column(name = "currency", nullable = false)
    private String currency = "INR";

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum RentalStatus {
        PENDING, PAID, RELEASED, COMPLETED, FAILED
    }
}
