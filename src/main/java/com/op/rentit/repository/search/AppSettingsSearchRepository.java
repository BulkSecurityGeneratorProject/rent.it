package com.op.rentit.repository.search;

import com.op.rentit.domain.AppSettings;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the AppSettings entity.
 */
public interface AppSettingsSearchRepository extends ElasticsearchRepository<AppSettings, Long> {
}
