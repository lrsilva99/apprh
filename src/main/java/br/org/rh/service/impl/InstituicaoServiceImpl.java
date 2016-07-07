package br.org.rh.service.impl;

import br.org.rh.service.InstituicaoService;
import br.org.rh.domain.Instituicao;
import br.org.rh.repository.InstituicaoRepository;
import br.org.rh.repository.search.InstituicaoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Instituicao.
 */
@Service
@Transactional
public class InstituicaoServiceImpl implements InstituicaoService{

    private final Logger log = LoggerFactory.getLogger(InstituicaoServiceImpl.class);
    
    @Inject
    private InstituicaoRepository instituicaoRepository;
    
    @Inject
    private InstituicaoSearchRepository instituicaoSearchRepository;
    
    /**
     * Save a instituicao.
     * 
     * @param instituicao the entity to save
     * @return the persisted entity
     */
    public Instituicao save(Instituicao instituicao) {
        log.debug("Request to save Instituicao : {}", instituicao);
        Instituicao result = instituicaoRepository.save(instituicao);
        instituicaoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the instituicaos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Instituicao> findAll(Pageable pageable) {
        log.debug("Request to get all Instituicaos");
        Page<Instituicao> result = instituicaoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one instituicao by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Instituicao findOne(Long id) {
        log.debug("Request to get Instituicao : {}", id);
        Instituicao instituicao = instituicaoRepository.findOne(id);
        return instituicao;
    }

    /**
     *  Delete the  instituicao by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Instituicao : {}", id);
        instituicaoRepository.delete(id);
        instituicaoSearchRepository.delete(id);
    }

    /**
     * Search for the instituicao corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instituicao> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Instituicaos for query {}", query);
        return instituicaoSearchRepository.search(queryStringQuery(query), pageable);
    }
}
