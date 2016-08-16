package com.op.rentit.repository;

import com.op.rentit.domain.Product;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings("unused")
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("select product from Product product where product.user.login = ?#{principal.username}")
    List<Product> findByUserIsCurrentUser();

    @Query("select distinct product from Product product left join fetch product.categories left join fetch product.tags")
    List<Product> findAllWithEagerRelationships();

    @Query("select product from Product product left join fetch product.categories left join fetch product.tags where product.id =:id")
    Product findOneWithEagerRelationships(@Param("id") Long id);

}
