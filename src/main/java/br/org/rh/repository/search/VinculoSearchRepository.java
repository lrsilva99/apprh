package br.org.rh.repository.search;

import br.org.rh.domain.Vinculo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Vinculo entity.
 */
public interface VinculoSearchRepository extends ElasticsearchRepository<Vinculo, Long> {
}
