package br.org.rh.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.org.rh.domain.Vinculo;
import br.org.rh.service.VinculoService;
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
 * REST controller for managing Vinculo.
 */
@RestController
@RequestMapping("/api")
public class VinculoResource {

    private final Logger log = LoggerFactory.getLogger(VinculoResource.class);
        
    @Inject
    private VinculoService vinculoService;
    
    /**
     * POST  /vinculos : Create a new vinculo.
     *
     * @param vinculo the vinculo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vinculo, or with status 400 (Bad Request) if the vinculo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/vinculos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vinculo> createVinculo(@Valid @RequestBody Vinculo vinculo) throws URISyntaxException {
        log.debug("REST request to save Vinculo : {}", vinculo);
        if (vinculo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("vinculo", "idexists", "A new vinculo cannot already have an ID")).body(null);
        }
        Vinculo result = vinculoService.save(vinculo);
        return ResponseEntity.created(new URI("/api/vinculos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("vinculo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /vinculos : Updates an existing vinculo.
     *
     * @param vinculo the vinculo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated vinculo,
     * or with status 400 (Bad Request) if the vinculo is not valid,
     * or with status 500 (Internal Server Error) if the vinculo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/vinculos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vinculo> updateVinculo(@Valid @RequestBody Vinculo vinculo) throws URISyntaxException {
        log.debug("REST request to update Vinculo : {}", vinculo);
        if (vinculo.getId() == null) {
            return createVinculo(vinculo);
        }
        Vinculo result = vinculoService.save(vinculo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("vinculo", vinculo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /vinculos : get all the vinculos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of vinculos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/vinculos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Vinculo>> getAllVinculos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Vinculos");
        Page<Vinculo> page = vinculoService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/vinculos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /vinculos/:id : get the "id" vinculo.
     *
     * @param id the id of the vinculo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the vinculo, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/vinculos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vinculo> getVinculo(@PathVariable Long id) {
        log.debug("REST request to get Vinculo : {}", id);
        Vinculo vinculo = vinculoService.findOne(id);
        return Optional.ofNullable(vinculo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /vinculos/:id : delete the "id" vinculo.
     *
     * @param id the id of the vinculo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/vinculos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteVinculo(@PathVariable Long id) {
        log.debug("REST request to delete Vinculo : {}", id);
        vinculoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("vinculo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/vinculos?query=:query : search for the vinculo corresponding
     * to the query.
     *
     * @param query the query of the vinculo search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/vinculos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Vinculo>> searchVinculos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Vinculos for query {}", query);
        Page<Vinculo> page = vinculoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/vinculos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
