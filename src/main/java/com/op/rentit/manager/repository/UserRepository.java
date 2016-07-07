package com.op.rentit.manager.repository;

import com.op.rentit.model.db.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
interface UserRepository extends CrudRepository<User, Long>{
}
