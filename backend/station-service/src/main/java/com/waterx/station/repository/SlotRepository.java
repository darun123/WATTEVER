package com.waterx.station.repository;

import com.waterx.station.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    Optional<Slot> findByStation_StationIdAndSlotNumber(String stationId, int slotNumber);
}
