package br.org.rh.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.org.rh.domain.Escolaridade;
import br.org.rh.service.EscolaridadeService;
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
 * REST controller for managing Escolaridade.
 */
@RestController
@RequestMapping("/api")
public class EscolaridadeResource {

    private final Logger log = LoggerFactory.getLogger(EscolaridadeResource.class);
        
    @Inject
    private EscolaridadeService escolaridadeService;
    
    /**
     * POST  /escolaridades : Create a new escolaridade.
     *
     * @param escolaridade the escolaridade to create
     * @return the ResponseEntity with status 201 (Created) and with body the new escolaridade, or with status 400 (Bad Request) if the escolaridade has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/escolaridades",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Escolaridade> createEscolaridade(@Valid @RequestBody Escolaridade escolaridade) throws URISyntaxException {
        log.debug("REST request to save Escolaridade : {}", escolaridade);
        if (escolaridade.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("escolaridade", "idexists", "A new escolaridade cannot already have an ID")).body(null);
        }
        Escolaridade result = escolaridadeService.save(escolaridade);
        return ResponseEntity.created(new URI("/api/escolaridades/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("escolaridade", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /escolaridades : Updates an existing escolaridade.
     *
     * @param escolaridade the escolaridade to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated escolaridade,
     * or with status 400 (Bad Request) if the escolaridade is not valid,
     * or with status 500 (Internal Server Error) if the escolaridade couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/escolaridades",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Escolaridade> updateEscolaridade(@Valid @RequestBody Escolaridade escolaridade) throws URISyntaxException {
        log.debug("REST request to update Escolaridade : {}", escolaridade);
        if (escolaridade.getId() == null) {
            return createEscolaridade(escolaridade);
        }
        Escolaridade result = escolaridadeService.save(escolaridade);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("escolaridade", escolaridade.getId().toString()))
            .body(result);
    }

    /**
     * GET  /escolaridades : get all the escolaridades.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of escolaridades in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/escolaridades",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Escolaridade>> getAllEscolaridades(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Escolaridades");
        Page<Escolaridade> page = escolaridadeService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/escolaridades");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /escolaridades/:id : get the "id" escolaridade.
     *
     * @param id the id of the escolaridade to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the escolaridade, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/escolaridades/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Escolaridade> getEscolaridade(@PathVariable Long id) {
        log.debug("REST request to get Escolaridade : {}", id);
        Escolaridade escolaridade = escolaridadeService.findOne(id);
        return Optional.ofNullable(escolaridade)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /escolaridades/:id : delete the "id" escolaridade.
     *
     * @param id the id of the escolaridade to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/escolaridades/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEscolaridade(@PathVariable Long id) {
        log.debug("REST request to delete Escolaridade : {}", id);
        escolaridadeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("escolaridade", id.toString())).build();
    }

    /**
     * SEARCH  /_search/escolaridades?query=:query : search for the escolaridade corresponding
     * to the query.
     *
     * @param query the query of the escolaridade search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/escolaridades",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Escolaridade>> searchEscolaridades(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Escolaridades for query {}", query);
        Page<Escolaridade> page = escolaridadeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/escolaridades");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
