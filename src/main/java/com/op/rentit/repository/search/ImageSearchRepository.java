package com.op.rentit.repository.search;

import com.op.rentit.domain.Image;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Image entity.
 */
public interface ImageSearchRepository extends ElasticsearchRepository<Image, Long> {
}
