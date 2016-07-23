package com.op.rentit.repository.search;

import com.op.rentit.domain.ProductAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProductAddress entity.
 */
public interface ProductAddressSearchRepository extends ElasticsearchRepository<ProductAddress, Long> {
}
