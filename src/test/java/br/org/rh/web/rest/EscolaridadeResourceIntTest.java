package br.org.rh.web.rest;

import br.org.rh.RhApp;
import br.org.rh.domain.Escolaridade;
import br.org.rh.repository.EscolaridadeRepository;
import br.org.rh.service.EscolaridadeService;
import br.org.rh.repository.search.EscolaridadeSearchRepository;

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
 * Test class for the EscolaridadeResource REST controller.
 *
 * @see EscolaridadeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RhApp.class)
@WebAppConfiguration
@IntegrationTest
public class EscolaridadeResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";
    private static final String DEFAULT_DESCRICAO = "AAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBB";

    @Inject
    private EscolaridadeRepository escolaridadeRepository;

    @Inject
    private EscolaridadeService escolaridadeService;

    @Inject
    private EscolaridadeSearchRepository escolaridadeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEscolaridadeMockMvc;

    private Escolaridade escolaridade;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EscolaridadeResource escolaridadeResource = new EscolaridadeResource();
        ReflectionTestUtils.setField(escolaridadeResource, "escolaridadeService", escolaridadeService);
        this.restEscolaridadeMockMvc = MockMvcBuilders.standaloneSetup(escolaridadeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        escolaridadeSearchRepository.deleteAll();
        escolaridade = new Escolaridade();
        escolaridade.setNome(DEFAULT_NOME);
        escolaridade.setDescricao(DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    public void createEscolaridade() throws Exception {
        int databaseSizeBeforeCreate = escolaridadeRepository.findAll().size();

        // Create the Escolaridade

        restEscolaridadeMockMvc.perform(post("/api/escolaridades")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(escolaridade)))
                .andExpect(status().isCreated());

        // Validate the Escolaridade in the database
        List<Escolaridade> escolaridades = escolaridadeRepository.findAll();
        assertThat(escolaridades).hasSize(databaseSizeBeforeCreate + 1);
        Escolaridade testEscolaridade = escolaridades.get(escolaridades.size() - 1);
        assertThat(testEscolaridade.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testEscolaridade.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);

        // Validate the Escolaridade in ElasticSearch
        Escolaridade escolaridadeEs = escolaridadeSearchRepository.findOne(testEscolaridade.getId());
        assertThat(escolaridadeEs).isEqualToComparingFieldByField(testEscolaridade);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = escolaridadeRepository.findAll().size();
        // set the field null
        escolaridade.setNome(null);

        // Create the Escolaridade, which fails.

        restEscolaridadeMockMvc.perform(post("/api/escolaridades")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(escolaridade)))
                .andExpect(status().isBadRequest());

        List<Escolaridade> escolaridades = escolaridadeRepository.findAll();
        assertThat(escolaridades).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescricaoIsRequired() throws Exception {
        int databaseSizeBeforeTest = escolaridadeRepository.findAll().size();
        // set the field null
        escolaridade.setDescricao(null);

        // Create the Escolaridade, which fails.

        restEscolaridadeMockMvc.perform(post("/api/escolaridades")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(escolaridade)))
                .andExpect(status().isBadRequest());

        List<Escolaridade> escolaridades = escolaridadeRepository.findAll();
        assertThat(escolaridades).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEscolaridades() throws Exception {
        // Initialize the database
        escolaridadeRepository.saveAndFlush(escolaridade);

        // Get all the escolaridades
        restEscolaridadeMockMvc.perform(get("/api/escolaridades?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(escolaridade.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void getEscolaridade() throws Exception {
        // Initialize the database
        escolaridadeRepository.saveAndFlush(escolaridade);

        // Get the escolaridade
        restEscolaridadeMockMvc.perform(get("/api/escolaridades/{id}", escolaridade.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(escolaridade.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEscolaridade() throws Exception {
        // Get the escolaridade
        restEscolaridadeMockMvc.perform(get("/api/escolaridades/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEscolaridade() throws Exception {
        // Initialize the database
        escolaridadeService.save(escolaridade);

        int databaseSizeBeforeUpdate = escolaridadeRepository.findAll().size();

        // Update the escolaridade
        Escolaridade updatedEscolaridade = new Escolaridade();
        updatedEscolaridade.setId(escolaridade.getId());
        updatedEscolaridade.setNome(UPDATED_NOME);
        updatedEscolaridade.setDescricao(UPDATED_DESCRICAO);

        restEscolaridadeMockMvc.perform(put("/api/escolaridades")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEscolaridade)))
                .andExpect(status().isOk());

        // Validate the Escolaridade in the database
        List<Escolaridade> escolaridades = escolaridadeRepository.findAll();
        assertThat(escolaridades).hasSize(databaseSizeBeforeUpdate);
        Escolaridade testEscolaridade = escolaridades.get(escolaridades.size() - 1);
        assertThat(testEscolaridade.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testEscolaridade.getDescricao()).isEqualTo(UPDATED_DESCRICAO);

        // Validate the Escolaridade in ElasticSearch
        Escolaridade escolaridadeEs = escolaridadeSearchRepository.findOne(testEscolaridade.getId());
        assertThat(escolaridadeEs).isEqualToComparingFieldByField(testEscolaridade);
    }

    @Test
    @Transactional
    public void deleteEscolaridade() throws Exception {
        // Initialize the database
        escolaridadeService.save(escolaridade);

        int databaseSizeBeforeDelete = escolaridadeRepository.findAll().size();

        // Get the escolaridade
        restEscolaridadeMockMvc.perform(delete("/api/escolaridades/{id}", escolaridade.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean escolaridadeExistsInEs = escolaridadeSearchRepository.exists(escolaridade.getId());
        assertThat(escolaridadeExistsInEs).isFalse();

        // Validate the database is empty
        List<Escolaridade> escolaridades = escolaridadeRepository.findAll();
        assertThat(escolaridades).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEscolaridade() throws Exception {
        // Initialize the database
        escolaridadeService.save(escolaridade);

        // Search the escolaridade
        restEscolaridadeMockMvc.perform(get("/api/_search/escolaridades?query=id:" + escolaridade.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(escolaridade.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }
}
