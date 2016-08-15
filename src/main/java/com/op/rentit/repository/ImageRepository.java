package com.op.rentit.repository;

import com.op.rentit.domain.Image;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Image entity.
 */
@SuppressWarnings("unused")
public interface ImageRepository extends JpaRepository<Image,Long> {

}
