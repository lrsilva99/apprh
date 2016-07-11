package br.org.rh.repository.search;

import br.org.rh.domain.Banco;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Banco entity.
 */
public interface BancoSearchRepository extends ElasticsearchRepository<Banco, Long> {
}
