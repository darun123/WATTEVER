package com.waterx.station.repository;

import com.waterx.station.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, String> {
    Optional<Station> findByQrCode(String qrCode);
}
