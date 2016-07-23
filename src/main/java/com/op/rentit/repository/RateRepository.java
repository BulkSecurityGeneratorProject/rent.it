package com.op.rentit.repository;

import com.op.rentit.domain.Rate;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Rate entity.
 */
@SuppressWarnings("unused")
public interface RateRepository extends JpaRepository<Rate,Long> {

}
