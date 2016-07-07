package br.org.rh.repository.search;

import br.org.rh.domain.Instituicao;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Instituicao entity.
 */
public interface InstituicaoSearchRepository extends ElasticsearchRepository<Instituicao, Long> {
}
