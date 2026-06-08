package com.waterx.rental.repository;

import com.waterx.rental.model.RentalSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<RentalSession, UUID> {
    Optional<RentalSession> findByBajieTradeNo(String bajieTradeNo);
}
