package com.op.rentit.repository;

import com.op.rentit.domain.WishList;

import org.springframework.data.jpa.repository.*;

import java.util.List;

@SuppressWarnings("unused")
public interface WishListRepository extends JpaRepository<WishList,Long> {

    @Query("select wishList from WishList wishList where wishList.user.login = ?#{principal.username}")
    List<WishList> findByUserIsCurrentUser();

}
