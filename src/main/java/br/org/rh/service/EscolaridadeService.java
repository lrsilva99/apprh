package br.org.rh.service;

import br.org.rh.domain.Escolaridade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Escolaridade.
 */
public interface EscolaridadeService {

    /**
     * Save a escolaridade.
     * 
     * @param escolaridade the entity to save
     * @return the persisted entity
     */
    Escolaridade save(Escolaridade escolaridade);

    /**
     *  Get all the escolaridades.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Escolaridade> findAll(Pageable pageable);

    /**
     *  Get the "id" escolaridade.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Escolaridade findOne(Long id);

    /**
     *  Delete the "id" escolaridade.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the escolaridade corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Escolaridade> search(String query, Pageable pageable);
}
