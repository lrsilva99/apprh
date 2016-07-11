package br.org.rh.service.impl;

import br.org.rh.service.BancoService;
import br.org.rh.domain.Banco;
import br.org.rh.repository.BancoRepository;
import br.org.rh.repository.search.BancoSearchRepository;
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
 * Service Implementation for managing Banco.
 */
@Service
@Transactional
public class BancoServiceImpl implements BancoService{

    private final Logger log = LoggerFactory.getLogger(BancoServiceImpl.class);
    
    @Inject
    private BancoRepository bancoRepository;
    
    @Inject
    private BancoSearchRepository bancoSearchRepository;
    
    /**
     * Save a banco.
     * 
     * @param banco the entity to save
     * @return the persisted entity
     */
    public Banco save(Banco banco) {
        log.debug("Request to save Banco : {}", banco);
        Banco result = bancoRepository.save(banco);
        bancoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the bancos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Banco> findAll(Pageable pageable) {
        log.debug("Request to get all Bancos");
        Page<Banco> result = bancoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one banco by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Banco findOne(Long id) {
        log.debug("Request to get Banco : {}", id);
        Banco banco = bancoRepository.findOne(id);
        return banco;
    }

    /**
     *  Delete the  banco by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Banco : {}", id);
        bancoRepository.delete(id);
        bancoSearchRepository.delete(id);
    }

    /**
     * Search for the banco corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Banco> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Bancos for query {}", query);
        return bancoSearchRepository.search(queryStringQuery(query), pageable);
    }
}
