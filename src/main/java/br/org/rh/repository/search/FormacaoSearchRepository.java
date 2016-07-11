package br.org.rh.repository.search;

import br.org.rh.domain.Formacao;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Formacao entity.
 */
public interface FormacaoSearchRepository extends ElasticsearchRepository<Formacao, Long> {
}
