package com.op.rentit.repository;

import com.op.rentit.domain.ProductAddress;

import org.springframework.data.jpa.repository.*;

import java.util.List;

@SuppressWarnings("unused")
public interface ProductAddressRepository extends JpaRepository<ProductAddress,Long> {

}
