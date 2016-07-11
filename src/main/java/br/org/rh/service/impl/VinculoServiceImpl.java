package br.org.rh.service.impl;

import br.org.rh.service.VinculoService;
import br.org.rh.domain.Vinculo;
import br.org.rh.repository.VinculoRepository;
import br.org.rh.repository.search.VinculoSearchRepository;
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
 * Service Implementation for managing Vinculo.
 */
@Service
@Transactional
public class VinculoServiceImpl implements VinculoService{

    private final Logger log = LoggerFactory.getLogger(VinculoServiceImpl.class);
    
    @Inject
    private VinculoRepository vinculoRepository;
    
    @Inject
    private VinculoSearchRepository vinculoSearchRepository;
    
    /**
     * Save a vinculo.
     * 
     * @param vinculo the entity to save
     * @return the persisted entity
     */
    public Vinculo save(Vinculo vinculo) {
        log.debug("Request to save Vinculo : {}", vinculo);
        Vinculo result = vinculoRepository.save(vinculo);
        vinculoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the vinculos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Vinculo> findAll(Pageable pageable) {
        log.debug("Request to get all Vinculos");
        Page<Vinculo> result = vinculoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one vinculo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Vinculo findOne(Long id) {
        log.debug("Request to get Vinculo : {}", id);
        Vinculo vinculo = vinculoRepository.findOne(id);
        return vinculo;
    }

    /**
     *  Delete the  vinculo by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Vinculo : {}", id);
        vinculoRepository.delete(id);
        vinculoSearchRepository.delete(id);
    }

    /**
     * Search for the vinculo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Vinculo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Vinculos for query {}", query);
        return vinculoSearchRepository.search(queryStringQuery(query), pageable);
    }
}
