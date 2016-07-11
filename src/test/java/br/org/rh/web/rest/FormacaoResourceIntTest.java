package br.org.rh.web.rest;

import br.org.rh.RhApp;
import br.org.rh.domain.Formacao;
import br.org.rh.repository.FormacaoRepository;
import br.org.rh.service.FormacaoService;
import br.org.rh.repository.search.FormacaoSearchRepository;

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
 * Test class for the FormacaoResource REST controller.
 *
 * @see FormacaoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RhApp.class)
@WebAppConfiguration
@IntegrationTest
public class FormacaoResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";
    private static final String DEFAULT_DESCRICAO = "AAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBB";

    @Inject
    private FormacaoRepository formacaoRepository;

    @Inject
    private FormacaoService formacaoService;

    @Inject
    private FormacaoSearchRepository formacaoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFormacaoMockMvc;

    private Formacao formacao;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FormacaoResource formacaoResource = new FormacaoResource();
        ReflectionTestUtils.setField(formacaoResource, "formacaoService", formacaoService);
        this.restFormacaoMockMvc = MockMvcBuilders.standaloneSetup(formacaoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        formacaoSearchRepository.deleteAll();
        formacao = new Formacao();
        formacao.setNome(DEFAULT_NOME);
        formacao.setDescricao(DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    public void createFormacao() throws Exception {
        int databaseSizeBeforeCreate = formacaoRepository.findAll().size();

        // Create the Formacao

        restFormacaoMockMvc.perform(post("/api/formacaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(formacao)))
                .andExpect(status().isCreated());

        // Validate the Formacao in the database
        List<Formacao> formacaos = formacaoRepository.findAll();
        assertThat(formacaos).hasSize(databaseSizeBeforeCreate + 1);
        Formacao testFormacao = formacaos.get(formacaos.size() - 1);
        assertThat(testFormacao.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testFormacao.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);

        // Validate the Formacao in ElasticSearch
        Formacao formacaoEs = formacaoSearchRepository.findOne(testFormacao.getId());
        assertThat(formacaoEs).isEqualToComparingFieldByField(testFormacao);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = formacaoRepository.findAll().size();
        // set the field null
        formacao.setNome(null);

        // Create the Formacao, which fails.

        restFormacaoMockMvc.perform(post("/api/formacaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(formacao)))
                .andExpect(status().isBadRequest());

        List<Formacao> formacaos = formacaoRepository.findAll();
        assertThat(formacaos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFormacaos() throws Exception {
        // Initialize the database
        formacaoRepository.saveAndFlush(formacao);

        // Get all the formacaos
        restFormacaoMockMvc.perform(get("/api/formacaos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(formacao.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void getFormacao() throws Exception {
        // Initialize the database
        formacaoRepository.saveAndFlush(formacao);

        // Get the formacao
        restFormacaoMockMvc.perform(get("/api/formacaos/{id}", formacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(formacao.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFormacao() throws Exception {
        // Get the formacao
        restFormacaoMockMvc.perform(get("/api/formacaos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFormacao() throws Exception {
        // Initialize the database
        formacaoService.save(formacao);

        int databaseSizeBeforeUpdate = formacaoRepository.findAll().size();

        // Update the formacao
        Formacao updatedFormacao = new Formacao();
        updatedFormacao.setId(formacao.getId());
        updatedFormacao.setNome(UPDATED_NOME);
        updatedFormacao.setDescricao(UPDATED_DESCRICAO);

        restFormacaoMockMvc.perform(put("/api/formacaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFormacao)))
                .andExpect(status().isOk());

        // Validate the Formacao in the database
        List<Formacao> formacaos = formacaoRepository.findAll();
        assertThat(formacaos).hasSize(databaseSizeBeforeUpdate);
        Formacao testFormacao = formacaos.get(formacaos.size() - 1);
        assertThat(testFormacao.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testFormacao.getDescricao()).isEqualTo(UPDATED_DESCRICAO);

        // Validate the Formacao in ElasticSearch
        Formacao formacaoEs = formacaoSearchRepository.findOne(testFormacao.getId());
        assertThat(formacaoEs).isEqualToComparingFieldByField(testFormacao);
    }

    @Test
    @Transactional
    public void deleteFormacao() throws Exception {
        // Initialize the database
        formacaoService.save(formacao);

        int databaseSizeBeforeDelete = formacaoRepository.findAll().size();

        // Get the formacao
        restFormacaoMockMvc.perform(delete("/api/formacaos/{id}", formacao.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean formacaoExistsInEs = formacaoSearchRepository.exists(formacao.getId());
        assertThat(formacaoExistsInEs).isFalse();

        // Validate the database is empty
        List<Formacao> formacaos = formacaoRepository.findAll();
        assertThat(formacaos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFormacao() throws Exception {
        // Initialize the database
        formacaoService.save(formacao);

        // Search the formacao
        restFormacaoMockMvc.perform(get("/api/_search/formacaos?query=id:" + formacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(formacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }
}
