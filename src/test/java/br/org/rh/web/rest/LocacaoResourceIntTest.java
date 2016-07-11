package br.org.rh.web.rest;

import br.org.rh.RhApp;
import br.org.rh.domain.Locacao;
import br.org.rh.repository.LocacaoRepository;
import br.org.rh.service.LocacaoService;
import br.org.rh.repository.search.LocacaoSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the LocacaoResource REST controller.
 *
 * @see LocacaoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RhApp.class)
@WebAppConfiguration
@IntegrationTest
public class LocacaoResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";
    private static final String DEFAULT_DESCRICAO = "AAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBB";

    @Inject
    private LocacaoRepository locacaoRepository;

    @Inject
    private LocacaoService locacaoService;

    @Inject
    private LocacaoSearchRepository locacaoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restLocacaoMockMvc;

    private Locacao locacao;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LocacaoResource locacaoResource = new LocacaoResource();
        ReflectionTestUtils.setField(locacaoResource, "locacaoService", locacaoService);
        this.restLocacaoMockMvc = MockMvcBuilders.standaloneSetup(locacaoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        locacaoSearchRepository.deleteAll();
        locacao = new Locacao();
        locacao.setNome(DEFAULT_NOME);
        locacao.setDescricao(DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    public void createLocacao() throws Exception {
        int databaseSizeBeforeCreate = locacaoRepository.findAll().size();

        // Create the Locacao

        restLocacaoMockMvc.perform(post("/api/locacaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locacao)))
                .andExpect(status().isCreated());

        // Validate the Locacao in the database
        List<Locacao> locacaos = locacaoRepository.findAll();
        assertThat(locacaos).hasSize(databaseSizeBeforeCreate + 1);
        Locacao testLocacao = locacaos.get(locacaos.size() - 1);
        assertThat(testLocacao.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testLocacao.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);

        // Validate the Locacao in ElasticSearch
        Locacao locacaoEs = locacaoSearchRepository.findOne(testLocacao.getId());
        assertThat(locacaoEs).isEqualToComparingFieldByField(testLocacao);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = locacaoRepository.findAll().size();
        // set the field null
        locacao.setNome(null);

        // Create the Locacao, which fails.

        restLocacaoMockMvc.perform(post("/api/locacaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locacao)))
                .andExpect(status().isBadRequest());

        List<Locacao> locacaos = locacaoRepository.findAll();
        assertThat(locacaos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLocacaos() throws Exception {
        // Initialize the database
        locacaoRepository.saveAndFlush(locacao);

        // Get all the locacaos
        restLocacaoMockMvc.perform(get("/api/locacaos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(locacao.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void getLocacao() throws Exception {
        // Initialize the database
        locacaoRepository.saveAndFlush(locacao);

        // Get the locacao
        restLocacaoMockMvc.perform(get("/api/locacaos/{id}", locacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(locacao.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLocacao() throws Exception {
        // Get the locacao
        restLocacaoMockMvc.perform(get("/api/locacaos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLocacao() throws Exception {
        // Initialize the database
        locacaoService.save(locacao);

        int databaseSizeBeforeUpdate = locacaoRepository.findAll().size();

        // Update the locacao
        Locacao updatedLocacao = new Locacao();
        updatedLocacao.setId(locacao.getId());
        updatedLocacao.setNome(UPDATED_NOME);
        updatedLocacao.setDescricao(UPDATED_DESCRICAO);

        restLocacaoMockMvc.perform(put("/api/locacaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLocacao)))
                .andExpect(status().isOk());

        // Validate the Locacao in the database
        List<Locacao> locacaos = locacaoRepository.findAll();
        assertThat(locacaos).hasSize(databaseSizeBeforeUpdate);
        Locacao testLocacao = locacaos.get(locacaos.size() - 1);
        assertThat(testLocacao.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testLocacao.getDescricao()).isEqualTo(UPDATED_DESCRICAO);

        // Validate the Locacao in ElasticSearch
        Locacao locacaoEs = locacaoSearchRepository.findOne(testLocacao.getId());
        assertThat(locacaoEs).isEqualToComparingFieldByField(testLocacao);
    }

    @Test
    @Transactional
    public void deleteLocacao() throws Exception {
        // Initialize the database
        locacaoService.save(locacao);

        int databaseSizeBeforeDelete = locacaoRepository.findAll().size();

        // Get the locacao
        restLocacaoMockMvc.perform(delete("/api/locacaos/{id}", locacao.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean locacaoExistsInEs = locacaoSearchRepository.exists(locacao.getId());
        assertThat(locacaoExistsInEs).isFalse();

        // Validate the database is empty
        List<Locacao> locacaos = locacaoRepository.findAll();
        assertThat(locacaos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLocacao() throws Exception {
        // Initialize the database
        locacaoService.save(locacao);

        // Search the locacao
        restLocacaoMockMvc.perform(get("/api/_search/locacaos?query=id:" + locacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }
}
