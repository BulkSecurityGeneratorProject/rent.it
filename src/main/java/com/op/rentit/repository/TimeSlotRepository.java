package com.op.rentit.repository;

import com.op.rentit.domain.TimeSlot;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TimeSlot entity.
 */
@SuppressWarnings("unused")
public interface TimeSlotRepository extends JpaRepository<TimeSlot,Long> {

}
