package br.org.rh.service;

import br.org.rh.domain.Banco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Banco.
 */
public interface BancoService {

    /**
     * Save a banco.
     * 
     * @param banco the entity to save
     * @return the persisted entity
     */
    Banco save(Banco banco);

    /**
     *  Get all the bancos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Banco> findAll(Pageable pageable);

    /**
     *  Get the "id" banco.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Banco findOne(Long id);

    /**
     *  Delete the "id" banco.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the banco corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Banco> search(String query, Pageable pageable);
}
