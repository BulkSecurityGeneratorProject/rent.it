package com.op.rentit.repository;

import com.op.rentit.domain.Tag;

import org.springframework.data.jpa.repository.*;

import java.util.List;

@SuppressWarnings("unused")
public interface TagRepository extends JpaRepository<Tag,Long> {

}
