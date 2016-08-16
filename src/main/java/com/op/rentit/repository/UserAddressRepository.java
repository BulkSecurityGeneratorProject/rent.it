package com.op.rentit.repository;

import com.op.rentit.domain.UserAddress;

import org.springframework.data.jpa.repository.*;

import java.util.List;

@SuppressWarnings("unused")
public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {

    @Query("select userAddress from UserAddress userAddress where userAddress.user.login = ?#{principal.username}")
    List<UserAddress> findByUserIsCurrentUser();

}
