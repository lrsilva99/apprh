package br.org.rh.service.impl;

import br.org.rh.service.LocacaoService;
import br.org.rh.domain.Locacao;
import br.org.rh.repository.LocacaoRepository;
import br.org.rh.repository.search.LocacaoSearchRepository;
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
 * Service Implementation for managing Locacao.
 */
@Service
@Transactional
public class LocacaoServiceImpl implements LocacaoService{

    private final Logger log = LoggerFactory.getLogger(LocacaoServiceImpl.class);
    
    @Inject
    private LocacaoRepository locacaoRepository;
    
    @Inject
    private LocacaoSearchRepository locacaoSearchRepository;
    
    /**
     * Save a locacao.
     * 
     * @param locacao the entity to save
     * @return the persisted entity
     */
    public Locacao save(Locacao locacao) {
        log.debug("Request to save Locacao : {}", locacao);
        Locacao result = locacaoRepository.save(locacao);
        locacaoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the locacaos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Locacao> findAll(Pageable pageable) {
        log.debug("Request to get all Locacaos");
        Page<Locacao> result = locacaoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one locacao by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Locacao findOne(Long id) {
        log.debug("Request to get Locacao : {}", id);
        Locacao locacao = locacaoRepository.findOne(id);
        return locacao;
    }

    /**
     *  Delete the  locacao by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Locacao : {}", id);
        locacaoRepository.delete(id);
        locacaoSearchRepository.delete(id);
    }

    /**
     * Search for the locacao corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Locacao> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Locacaos for query {}", query);
        return locacaoSearchRepository.search(queryStringQuery(query), pageable);
    }
}
