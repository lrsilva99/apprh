package br.org.rh.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.org.rh.domain.Instituicao;
import br.org.rh.service.InstituicaoService;
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
 * REST controller for managing Instituicao.
 */
@RestController
@RequestMapping("/api")
public class InstituicaoResource {

    private final Logger log = LoggerFactory.getLogger(InstituicaoResource.class);
        
    @Inject
    private InstituicaoService instituicaoService;
    
    /**
     * POST  /instituicaos : Create a new instituicao.
     *
     * @param instituicao the instituicao to create
     * @return the ResponseEntity with status 201 (Created) and with body the new instituicao, or with status 400 (Bad Request) if the instituicao has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/instituicaos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Instituicao> createInstituicao(@Valid @RequestBody Instituicao instituicao) throws URISyntaxException {
        log.debug("REST request to save Instituicao : {}", instituicao);
        if (instituicao.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("instituicao", "idexists", "A new instituicao cannot already have an ID")).body(null);
        }
        Instituicao result = instituicaoService.save(instituicao);
        return ResponseEntity.created(new URI("/api/instituicaos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("instituicao", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /instituicaos : Updates an existing instituicao.
     *
     * @param instituicao the instituicao to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated instituicao,
     * or with status 400 (Bad Request) if the instituicao is not valid,
     * or with status 500 (Internal Server Error) if the instituicao couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/instituicaos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Instituicao> updateInstituicao(@Valid @RequestBody Instituicao instituicao) throws URISyntaxException {
        log.debug("REST request to update Instituicao : {}", instituicao);
        if (instituicao.getId() == null) {
            return createInstituicao(instituicao);
        }
        Instituicao result = instituicaoService.save(instituicao);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("instituicao", instituicao.getId().toString()))
            .body(result);
    }

    /**
     * GET  /instituicaos : get all the instituicaos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of instituicaos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/instituicaos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instituicao>> getAllInstituicaos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Instituicaos");
        Page<Instituicao> page = instituicaoService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/instituicaos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /instituicaos/:id : get the "id" instituicao.
     *
     * @param id the id of the instituicao to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the instituicao, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/instituicaos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Instituicao> getInstituicao(@PathVariable Long id) {
        log.debug("REST request to get Instituicao : {}", id);
        Instituicao instituicao = instituicaoService.findOne(id);
        return Optional.ofNullable(instituicao)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /instituicaos/:id : delete the "id" instituicao.
     *
     * @param id the id of the instituicao to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/instituicaos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteInstituicao(@PathVariable Long id) {
        log.debug("REST request to delete Instituicao : {}", id);
        instituicaoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("instituicao", id.toString())).build();
    }

    /**
     * SEARCH  /_search/instituicaos?query=:query : search for the instituicao corresponding
     * to the query.
     *
     * @param query the query of the instituicao search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/instituicaos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instituicao>> searchInstituicaos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Instituicaos for query {}", query);
        Page<Instituicao> page = instituicaoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/instituicaos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
