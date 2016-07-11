package br.org.rh.repository.search;

import br.org.rh.domain.Cargo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Cargo entity.
 */
public interface CargoSearchRepository extends ElasticsearchRepository<Cargo, Long> {
}
