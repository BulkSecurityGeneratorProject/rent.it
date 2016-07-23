package com.op.rentit.repository;

import com.op.rentit.domain.ProductAddress;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProductAddress entity.
 */
@SuppressWarnings("unused")
public interface ProductAddressRepository extends JpaRepository<ProductAddress,Long> {

}
