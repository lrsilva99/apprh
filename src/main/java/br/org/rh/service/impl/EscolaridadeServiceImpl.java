package br.org.rh.service.impl;

import br.org.rh.service.EscolaridadeService;
import br.org.rh.domain.Escolaridade;
import br.org.rh.repository.EscolaridadeRepository;
import br.org.rh.repository.search.EscolaridadeSearchRepository;
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
 * Service Implementation for managing Escolaridade.
 */
@Service
@Transactional
public class EscolaridadeServiceImpl implements EscolaridadeService{

    private final Logger log = LoggerFactory.getLogger(EscolaridadeServiceImpl.class);
    
    @Inject
    private EscolaridadeRepository escolaridadeRepository;
    
    @Inject
    private EscolaridadeSearchRepository escolaridadeSearchRepository;
    
    /**
     * Save a escolaridade.
     * 
     * @param escolaridade the entity to save
     * @return the persisted entity
     */
    public Escolaridade save(Escolaridade escolaridade) {
        log.debug("Request to save Escolaridade : {}", escolaridade);
        Escolaridade result = escolaridadeRepository.save(escolaridade);
        escolaridadeSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the escolaridades.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Escolaridade> findAll(Pageable pageable) {
        log.debug("Request to get all Escolaridades");
        Page<Escolaridade> result = escolaridadeRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one escolaridade by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Escolaridade findOne(Long id) {
        log.debug("Request to get Escolaridade : {}", id);
        Escolaridade escolaridade = escolaridadeRepository.findOne(id);
        return escolaridade;
    }

    /**
     *  Delete the  escolaridade by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Escolaridade : {}", id);
        escolaridadeRepository.delete(id);
        escolaridadeSearchRepository.delete(id);
    }

    /**
     * Search for the escolaridade corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Escolaridade> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Escolaridades for query {}", query);
        return escolaridadeSearchRepository.search(queryStringQuery(query), pageable);
    }
}
