package com.op.rentit.repository.search;

import com.op.rentit.domain.Booking;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Booking entity.
 */
public interface BookingSearchRepository extends ElasticsearchRepository<Booking, Long> {
}
