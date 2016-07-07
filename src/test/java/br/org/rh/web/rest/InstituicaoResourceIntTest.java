package br.org.rh.web.rest;

import br.org.rh.RhApp;
import br.org.rh.domain.Instituicao;
import br.org.rh.repository.InstituicaoRepository;
import br.org.rh.service.InstituicaoService;
import br.org.rh.repository.search.InstituicaoSearchRepository;

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
 * Test class for the InstituicaoResource REST controller.
 *
 * @see InstituicaoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RhApp.class)
@WebAppConfiguration
@IntegrationTest
public class InstituicaoResourceIntTest {

    private static final String DEFAULT_SIGLA = "AAAAA";
    private static final String UPDATED_SIGLA = "BBBBB";
    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";
    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";
    private static final String DEFAULT_TELEFONE = "AAAAA";
    private static final String UPDATED_TELEFONE = "BBBBB";
    private static final String DEFAULT_ENDERECO = "AAAAA";
    private static final String UPDATED_ENDERECO = "BBBBB";

    @Inject
    private InstituicaoRepository instituicaoRepository;

    @Inject
    private InstituicaoService instituicaoService;

    @Inject
    private InstituicaoSearchRepository instituicaoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restInstituicaoMockMvc;

    private Instituicao instituicao;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        InstituicaoResource instituicaoResource = new InstituicaoResource();
        ReflectionTestUtils.setField(instituicaoResource, "instituicaoService", instituicaoService);
        this.restInstituicaoMockMvc = MockMvcBuilders.standaloneSetup(instituicaoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        instituicaoSearchRepository.deleteAll();
        instituicao = new Instituicao();
        instituicao.setSigla(DEFAULT_SIGLA);
        instituicao.setNome(DEFAULT_NOME);
        instituicao.setEmail(DEFAULT_EMAIL);
        instituicao.setTelefone(DEFAULT_TELEFONE);
        instituicao.setEndereco(DEFAULT_ENDERECO);
    }

    @Test
    @Transactional
    public void createInstituicao() throws Exception {
        int databaseSizeBeforeCreate = instituicaoRepository.findAll().size();

        // Create the Instituicao

        restInstituicaoMockMvc.perform(post("/api/instituicaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instituicao)))
                .andExpect(status().isCreated());

        // Validate the Instituicao in the database
        List<Instituicao> instituicaos = instituicaoRepository.findAll();
        assertThat(instituicaos).hasSize(databaseSizeBeforeCreate + 1);
        Instituicao testInstituicao = instituicaos.get(instituicaos.size() - 1);
        assertThat(testInstituicao.getSigla()).isEqualTo(DEFAULT_SIGLA);
        assertThat(testInstituicao.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testInstituicao.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testInstituicao.getTelefone()).isEqualTo(DEFAULT_TELEFONE);
        assertThat(testInstituicao.getEndereco()).isEqualTo(DEFAULT_ENDERECO);

        // Validate the Instituicao in ElasticSearch
        Instituicao instituicaoEs = instituicaoSearchRepository.findOne(testInstituicao.getId());
        assertThat(instituicaoEs).isEqualToComparingFieldByField(testInstituicao);
    }

    @Test
    @Transactional
    public void checkSiglaIsRequired() throws Exception {
        int databaseSizeBeforeTest = instituicaoRepository.findAll().size();
        // set the field null
        instituicao.setSigla(null);

        // Create the Instituicao, which fails.

        restInstituicaoMockMvc.perform(post("/api/instituicaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instituicao)))
                .andExpect(status().isBadRequest());

        List<Instituicao> instituicaos = instituicaoRepository.findAll();
        assertThat(instituicaos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = instituicaoRepository.findAll().size();
        // set the field null
        instituicao.setNome(null);

        // Create the Instituicao, which fails.

        restInstituicaoMockMvc.perform(post("/api/instituicaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instituicao)))
                .andExpect(status().isBadRequest());

        List<Instituicao> instituicaos = instituicaoRepository.findAll();
        assertThat(instituicaos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = instituicaoRepository.findAll().size();
        // set the field null
        instituicao.setEmail(null);

        // Create the Instituicao, which fails.

        restInstituicaoMockMvc.perform(post("/api/instituicaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instituicao)))
                .andExpect(status().isBadRequest());

        List<Instituicao> instituicaos = instituicaoRepository.findAll();
        assertThat(instituicaos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInstituicaos() throws Exception {
        // Initialize the database
        instituicaoRepository.saveAndFlush(instituicao);

        // Get all the instituicaos
        restInstituicaoMockMvc.perform(get("/api/instituicaos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(instituicao.getId().intValue())))
                .andExpect(jsonPath("$.[*].sigla").value(hasItem(DEFAULT_SIGLA.toString())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE.toString())))
                .andExpect(jsonPath("$.[*].endereco").value(hasItem(DEFAULT_ENDERECO.toString())));
    }

    @Test
    @Transactional
    public void getInstituicao() throws Exception {
        // Initialize the database
        instituicaoRepository.saveAndFlush(instituicao);

        // Get the instituicao
        restInstituicaoMockMvc.perform(get("/api/instituicaos/{id}", instituicao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(instituicao.getId().intValue()))
            .andExpect(jsonPath("$.sigla").value(DEFAULT_SIGLA.toString()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.telefone").value(DEFAULT_TELEFONE.toString()))
            .andExpect(jsonPath("$.endereco").value(DEFAULT_ENDERECO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingInstituicao() throws Exception {
        // Get the instituicao
        restInstituicaoMockMvc.perform(get("/api/instituicaos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInstituicao() throws Exception {
        // Initialize the database
        instituicaoService.save(instituicao);

        int databaseSizeBeforeUpdate = instituicaoRepository.findAll().size();

        // Update the instituicao
        Instituicao updatedInstituicao = new Instituicao();
        updatedInstituicao.setId(instituicao.getId());
        updatedInstituicao.setSigla(UPDATED_SIGLA);
        updatedInstituicao.setNome(UPDATED_NOME);
        updatedInstituicao.setEmail(UPDATED_EMAIL);
        updatedInstituicao.setTelefone(UPDATED_TELEFONE);
        updatedInstituicao.setEndereco(UPDATED_ENDERECO);

        restInstituicaoMockMvc.perform(put("/api/instituicaos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedInstituicao)))
                .andExpect(status().isOk());

        // Validate the Instituicao in the database
        List<Instituicao> instituicaos = instituicaoRepository.findAll();
        assertThat(instituicaos).hasSize(databaseSizeBeforeUpdate);
        Instituicao testInstituicao = instituicaos.get(instituicaos.size() - 1);
        assertThat(testInstituicao.getSigla()).isEqualTo(UPDATED_SIGLA);
        assertThat(testInstituicao.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testInstituicao.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testInstituicao.getTelefone()).isEqualTo(UPDATED_TELEFONE);
        assertThat(testInstituicao.getEndereco()).isEqualTo(UPDATED_ENDERECO);

        // Validate the Instituicao in ElasticSearch
        Instituicao instituicaoEs = instituicaoSearchRepository.findOne(testInstituicao.getId());
        assertThat(instituicaoEs).isEqualToComparingFieldByField(testInstituicao);
    }

    @Test
    @Transactional
    public void deleteInstituicao() throws Exception {
        // Initialize the database
        instituicaoService.save(instituicao);

        int databaseSizeBeforeDelete = instituicaoRepository.findAll().size();

        // Get the instituicao
        restInstituicaoMockMvc.perform(delete("/api/instituicaos/{id}", instituicao.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean instituicaoExistsInEs = instituicaoSearchRepository.exists(instituicao.getId());
        assertThat(instituicaoExistsInEs).isFalse();

        // Validate the database is empty
        List<Instituicao> instituicaos = instituicaoRepository.findAll();
        assertThat(instituicaos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchInstituicao() throws Exception {
        // Initialize the database
        instituicaoService.save(instituicao);

        // Search the instituicao
        restInstituicaoMockMvc.perform(get("/api/_search/instituicaos?query=id:" + instituicao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instituicao.getId().intValue())))
            .andExpect(jsonPath("$.[*].sigla").value(hasItem(DEFAULT_SIGLA.toString())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE.toString())))
            .andExpect(jsonPath("$.[*].endereco").value(hasItem(DEFAULT_ENDERECO.toString())));
    }
}
