package com.op.rentit.repository.search;

import com.op.rentit.domain.Rate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Rate entity.
 */
public interface RateSearchRepository extends ElasticsearchRepository<Rate, Long> {
}
