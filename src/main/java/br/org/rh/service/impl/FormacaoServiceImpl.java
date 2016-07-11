package br.org.rh.service.impl;

import br.org.rh.service.FormacaoService;
import br.org.rh.domain.Formacao;
import br.org.rh.repository.FormacaoRepository;
import br.org.rh.repository.search.FormacaoSearchRepository;
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
 * Service Implementation for managing Formacao.
 */
@Service
@Transactional
public class FormacaoServiceImpl implements FormacaoService{

    private final Logger log = LoggerFactory.getLogger(FormacaoServiceImpl.class);
    
    @Inject
    private FormacaoRepository formacaoRepository;
    
    @Inject
    private FormacaoSearchRepository formacaoSearchRepository;
    
    /**
     * Save a formacao.
     * 
     * @param formacao the entity to save
     * @return the persisted entity
     */
    public Formacao save(Formacao formacao) {
        log.debug("Request to save Formacao : {}", formacao);
        Formacao result = formacaoRepository.save(formacao);
        formacaoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the formacaos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Formacao> findAll(Pageable pageable) {
        log.debug("Request to get all Formacaos");
        Page<Formacao> result = formacaoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one formacao by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Formacao findOne(Long id) {
        log.debug("Request to get Formacao : {}", id);
        Formacao formacao = formacaoRepository.findOne(id);
        return formacao;
    }

    /**
     *  Delete the  formacao by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Formacao : {}", id);
        formacaoRepository.delete(id);
        formacaoSearchRepository.delete(id);
    }

    /**
     * Search for the formacao corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Formacao> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Formacaos for query {}", query);
        return formacaoSearchRepository.search(queryStringQuery(query), pageable);
    }
}
