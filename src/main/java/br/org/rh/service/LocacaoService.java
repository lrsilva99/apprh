package br.org.rh.service;

import br.org.rh.domain.Locacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Locacao.
 */
public interface LocacaoService {

    /**
     * Save a locacao.
     * 
     * @param locacao the entity to save
     * @return the persisted entity
     */
    Locacao save(Locacao locacao);

    /**
     *  Get all the locacaos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Locacao> findAll(Pageable pageable);

    /**
     *  Get the "id" locacao.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Locacao findOne(Long id);

    /**
     *  Delete the "id" locacao.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the locacao corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Locacao> search(String query, Pageable pageable);
}
