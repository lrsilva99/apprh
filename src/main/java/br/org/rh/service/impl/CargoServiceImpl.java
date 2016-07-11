package br.org.rh.service.impl;

import br.org.rh.service.CargoService;
import br.org.rh.domain.Cargo;
import br.org.rh.repository.CargoRepository;
import br.org.rh.repository.search.CargoSearchRepository;
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
 * Service Implementation for managing Cargo.
 */
@Service
@Transactional
public class CargoServiceImpl implements CargoService{

    private final Logger log = LoggerFactory.getLogger(CargoServiceImpl.class);
    
    @Inject
    private CargoRepository cargoRepository;
    
    @Inject
    private CargoSearchRepository cargoSearchRepository;
    
    /**
     * Save a cargo.
     * 
     * @param cargo the entity to save
     * @return the persisted entity
     */
    public Cargo save(Cargo cargo) {
        log.debug("Request to save Cargo : {}", cargo);
        Cargo result = cargoRepository.save(cargo);
        cargoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the cargos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Cargo> findAll(Pageable pageable) {
        log.debug("Request to get all Cargos");
        Page<Cargo> result = cargoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one cargo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Cargo findOne(Long id) {
        log.debug("Request to get Cargo : {}", id);
        Cargo cargo = cargoRepository.findOne(id);
        return cargo;
    }

    /**
     *  Delete the  cargo by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Cargo : {}", id);
        cargoRepository.delete(id);
        cargoSearchRepository.delete(id);
    }

    /**
     * Search for the cargo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Cargo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Cargos for query {}", query);
        return cargoSearchRepository.search(queryStringQuery(query), pageable);
    }
}
