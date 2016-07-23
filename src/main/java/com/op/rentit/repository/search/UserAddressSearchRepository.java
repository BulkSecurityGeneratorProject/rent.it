package com.op.rentit.repository.search;

import com.op.rentit.domain.UserAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the UserAddress entity.
 */
public interface UserAddressSearchRepository extends ElasticsearchRepository<UserAddress, Long> {
}
