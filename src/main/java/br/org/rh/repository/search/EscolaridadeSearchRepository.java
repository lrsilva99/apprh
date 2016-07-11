package br.org.rh.repository.search;

import br.org.rh.domain.Escolaridade;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Escolaridade entity.
 */
public interface EscolaridadeSearchRepository extends ElasticsearchRepository<Escolaridade, Long> {
}
