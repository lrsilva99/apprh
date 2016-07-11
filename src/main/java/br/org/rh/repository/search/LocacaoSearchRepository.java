package br.org.rh.repository.search;

import br.org.rh.domain.Locacao;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Locacao entity.
 */
public interface LocacaoSearchRepository extends ElasticsearchRepository<Locacao, Long> {
}
