package com.op.rentit.repository.search;

import com.op.rentit.domain.TimeSlot;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TimeSlot entity.
 */
public interface TimeSlotSearchRepository extends ElasticsearchRepository<TimeSlot, Long> {
}
