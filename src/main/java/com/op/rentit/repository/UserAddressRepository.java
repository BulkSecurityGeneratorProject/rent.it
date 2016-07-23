package com.op.rentit.repository;

import com.op.rentit.domain.UserAddress;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the UserAddress entity.
 */
@SuppressWarnings("unused")
public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {

}
