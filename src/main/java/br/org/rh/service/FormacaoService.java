package br.org.rh.service;

import br.org.rh.domain.Formacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Formacao.
 */
public interface FormacaoService {

    /**
     * Save a formacao.
     * 
     * @param formacao the entity to save
     * @return the persisted entity
     */
    Formacao save(Formacao formacao);

    /**
     *  Get all the formacaos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Formacao> findAll(Pageable pageable);

    /**
     *  Get the "id" formacao.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Formacao findOne(Long id);

    /**
     *  Delete the "id" formacao.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the formacao corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Formacao> search(String query, Pageable pageable);
}
