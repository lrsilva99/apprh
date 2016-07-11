package br.org.rh.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.org.rh.domain.Locacao;
import br.org.rh.service.LocacaoService;
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
 * REST controller for managing Locacao.
 */
@RestController
@RequestMapping("/api")
public class LocacaoResource {

    private final Logger log = LoggerFactory.getLogger(LocacaoResource.class);
        
    @Inject
    private LocacaoService locacaoService;
    
    /**
     * POST  /locacaos : Create a new locacao.
     *
     * @param locacao the locacao to create
     * @return the ResponseEntity with status 201 (Created) and with body the new locacao, or with status 400 (Bad Request) if the locacao has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/locacaos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Locacao> createLocacao(@Valid @RequestBody Locacao locacao) throws URISyntaxException {
        log.debug("REST request to save Locacao : {}", locacao);
        if (locacao.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("locacao", "idexists", "A new locacao cannot already have an ID")).body(null);
        }
        Locacao result = locacaoService.save(locacao);
        return ResponseEntity.created(new URI("/api/locacaos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("locacao", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /locacaos : Updates an existing locacao.
     *
     * @param locacao the locacao to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated locacao,
     * or with status 400 (Bad Request) if the locacao is not valid,
     * or with status 500 (Internal Server Error) if the locacao couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/locacaos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Locacao> updateLocacao(@Valid @RequestBody Locacao locacao) throws URISyntaxException {
        log.debug("REST request to update Locacao : {}", locacao);
        if (locacao.getId() == null) {
            return createLocacao(locacao);
        }
        Locacao result = locacaoService.save(locacao);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("locacao", locacao.getId().toString()))
            .body(result);
    }

    /**
     * GET  /locacaos : get all the locacaos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of locacaos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/locacaos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Locacao>> getAllLocacaos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Locacaos");
        Page<Locacao> page = locacaoService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/locacaos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /locacaos/:id : get the "id" locacao.
     *
     * @param id the id of the locacao to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the locacao, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/locacaos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Locacao> getLocacao(@PathVariable Long id) {
        log.debug("REST request to get Locacao : {}", id);
        Locacao locacao = locacaoService.findOne(id);
        return Optional.ofNullable(locacao)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /locacaos/:id : delete the "id" locacao.
     *
     * @param id the id of the locacao to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/locacaos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteLocacao(@PathVariable Long id) {
        log.debug("REST request to delete Locacao : {}", id);
        locacaoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("locacao", id.toString())).build();
    }

    /**
     * SEARCH  /_search/locacaos?query=:query : search for the locacao corresponding
     * to the query.
     *
     * @param query the query of the locacao search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/locacaos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Locacao>> searchLocacaos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Locacaos for query {}", query);
        Page<Locacao> page = locacaoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/locacaos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
