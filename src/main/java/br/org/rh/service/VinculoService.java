package br.org.rh.service;

import br.org.rh.domain.Vinculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Vinculo.
 */
public interface VinculoService {

    /**
     * Save a vinculo.
     * 
     * @param vinculo the entity to save
     * @return the persisted entity
     */
    Vinculo save(Vinculo vinculo);

    /**
     *  Get all the vinculos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Vinculo> findAll(Pageable pageable);

    /**
     *  Get the "id" vinculo.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Vinculo findOne(Long id);

    /**
     *  Delete the "id" vinculo.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the vinculo corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Vinculo> search(String query, Pageable pageable);
}
