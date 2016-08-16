package com.op.rentit.repository;

import com.op.rentit.domain.Rate;

import org.springframework.data.jpa.repository.*;

import java.util.List;

@SuppressWarnings("unused")
public interface RateRepository extends JpaRepository<Rate,Long> {

}
