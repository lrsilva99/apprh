package br.org.rh.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.org.rh.domain.Cargo;
import br.org.rh.service.CargoService;
import br.org.rh.web.rest.util.HeaderUtil;
import br.org.rh.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Cargo.
 */
@RestController
@RequestMapping("/api")
public class CargoResource {

    private final Logger log = LoggerFactory.getLogger(CargoResource.class);
        
    @Inject
    private CargoService cargoService;
    
    /**
     * POST  /cargos : Create a new cargo.
     *
     * @param cargo the cargo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cargo, or with status 400 (Bad Request) if the cargo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cargos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cargo> createCargo(@Valid @RequestBody Cargo cargo) throws URISyntaxException {
        log.debug("REST request to save Cargo : {}", cargo);
        if (cargo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("cargo", "idexists", "A new cargo cannot already have an ID")).body(null);
        }
        Cargo result = cargoService.save(cargo);
        return ResponseEntity.created(new URI("/api/cargos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("cargo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cargos : Updates an existing cargo.
     *
     * @param cargo the cargo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cargo,
     * or with status 400 (Bad Request) if the cargo is not valid,
     * or with status 500 (Internal Server Error) if the cargo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cargos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cargo> updateCargo(@Valid @RequestBody Cargo cargo) throws URISyntaxException {
        log.debug("REST request to update Cargo : {}", cargo);
        if (cargo.getId() == null) {
            return createCargo(cargo);
        }
        Cargo result = cargoService.save(cargo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("cargo", cargo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cargos : get all the cargos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of cargos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/cargos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Cargo>> getAllCargos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Cargos");
        Page<Cargo> page = cargoService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cargos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /cargos/:id : get the "id" cargo.
     *
     * @param id the id of the cargo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cargo, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/cargos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cargo> getCargo(@PathVariable Long id) {
        log.debug("REST request to get Cargo : {}", id);
        Cargo cargo = cargoService.findOne(id);
        return Optional.ofNullable(cargo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /cargos/:id : delete the "id" cargo.
     *
     * @param id the id of the cargo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/cargos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCargo(@PathVariable Long id) {
        log.debug("REST request to delete Cargo : {}", id);
        cargoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("cargo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/cargos?query=:query : search for the cargo corresponding
     * to the query.
     *
     * @param query the query of the cargo search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/cargos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Cargo>> searchCargos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Cargos for query {}", query);
        Page<Cargo> page = cargoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/cargos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
