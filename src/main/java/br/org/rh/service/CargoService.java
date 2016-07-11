package br.org.rh.service;

import br.org.rh.domain.Cargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Cargo.
 */
public interface CargoService {

    /**
     * Save a cargo.
     * 
     * @param cargo the entity to save
     * @return the persisted entity
     */
    Cargo save(Cargo cargo);

    /**
     *  Get all the cargos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Cargo> findAll(Pageable pageable);

    /**
     *  Get the "id" cargo.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Cargo findOne(Long id);

    /**
     *  Delete the "id" cargo.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the cargo corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Cargo> search(String query, Pageable pageable);
}
