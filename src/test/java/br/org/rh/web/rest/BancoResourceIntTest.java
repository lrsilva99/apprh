package br.org.rh.web.rest;

import br.org.rh.RhApp;
import br.org.rh.domain.Banco;
import br.org.rh.repository.BancoRepository;
import br.org.rh.service.BancoService;
import br.org.rh.repository.search.BancoSearchRepository;

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
 * Test class for the BancoResource REST controller.
 *
 * @see BancoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RhApp.class)
@WebAppConfiguration
@IntegrationTest
public class BancoResourceIntTest {

    private static final String DEFAULT_CODIGO = "AAAAA";
    private static final String UPDATED_CODIGO = "BBBBB";
    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";

    @Inject
    private BancoRepository bancoRepository;

    @Inject
    private BancoService bancoService;

    @Inject
    private BancoSearchRepository bancoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBancoMockMvc;

    private Banco banco;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BancoResource bancoResource = new BancoResource();
        ReflectionTestUtils.setField(bancoResource, "bancoService", bancoService);
        this.restBancoMockMvc = MockMvcBuilders.standaloneSetup(bancoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        bancoSearchRepository.deleteAll();
        banco = new Banco();
        banco.setCodigo(DEFAULT_CODIGO);
        banco.setNome(DEFAULT_NOME);
    }

    @Test
    @Transactional
    public void createBanco() throws Exception {
        int databaseSizeBeforeCreate = bancoRepository.findAll().size();

        // Create the Banco

        restBancoMockMvc.perform(post("/api/bancos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(banco)))
                .andExpect(status().isCreated());

        // Validate the Banco in the database
        List<Banco> bancos = bancoRepository.findAll();
        assertThat(bancos).hasSize(databaseSizeBeforeCreate + 1);
        Banco testBanco = bancos.get(bancos.size() - 1);
        assertThat(testBanco.getCodigo()).isEqualTo(DEFAULT_CODIGO);
        assertThat(testBanco.getNome()).isEqualTo(DEFAULT_NOME);

        // Validate the Banco in ElasticSearch
        Banco bancoEs = bancoSearchRepository.findOne(testBanco.getId());
        assertThat(bancoEs).isEqualToComparingFieldByField(testBanco);
    }

    @Test
    @Transactional
    public void checkCodigoIsRequired() throws Exception {
        int databaseSizeBeforeTest = bancoRepository.findAll().size();
        // set the field null
        banco.setCodigo(null);

        // Create the Banco, which fails.

        restBancoMockMvc.perform(post("/api/bancos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(banco)))
                .andExpect(status().isBadRequest());

        List<Banco> bancos = bancoRepository.findAll();
        assertThat(bancos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bancoRepository.findAll().size();
        // set the field null
        banco.setNome(null);

        // Create the Banco, which fails.

        restBancoMockMvc.perform(post("/api/bancos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(banco)))
                .andExpect(status().isBadRequest());

        List<Banco> bancos = bancoRepository.findAll();
        assertThat(bancos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBancos() throws Exception {
        // Initialize the database
        bancoRepository.saveAndFlush(banco);

        // Get all the bancos
        restBancoMockMvc.perform(get("/api/bancos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(banco.getId().intValue())))
                .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.toString())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())));
    }

    @Test
    @Transactional
    public void getBanco() throws Exception {
        // Initialize the database
        bancoRepository.saveAndFlush(banco);

        // Get the banco
        restBancoMockMvc.perform(get("/api/bancos/{id}", banco.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(banco.getId().intValue()))
            .andExpect(jsonPath("$.codigo").value(DEFAULT_CODIGO.toString()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBanco() throws Exception {
        // Get the banco
        restBancoMockMvc.perform(get("/api/bancos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBanco() throws Exception {
        // Initialize the database
        bancoService.save(banco);

        int databaseSizeBeforeUpdate = bancoRepository.findAll().size();

        // Update the banco
        Banco updatedBanco = new Banco();
        updatedBanco.setId(banco.getId());
        updatedBanco.setCodigo(UPDATED_CODIGO);
        updatedBanco.setNome(UPDATED_NOME);

        restBancoMockMvc.perform(put("/api/bancos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBanco)))
                .andExpect(status().isOk());

        // Validate the Banco in the database
        List<Banco> bancos = bancoRepository.findAll();
        assertThat(bancos).hasSize(databaseSizeBeforeUpdate);
        Banco testBanco = bancos.get(bancos.size() - 1);
        assertThat(testBanco.getCodigo()).isEqualTo(UPDATED_CODIGO);
        assertThat(testBanco.getNome()).isEqualTo(UPDATED_NOME);

        // Validate the Banco in ElasticSearch
        Banco bancoEs = bancoSearchRepository.findOne(testBanco.getId());
        assertThat(bancoEs).isEqualToComparingFieldByField(testBanco);
    }

    @Test
    @Transactional
    public void deleteBanco() throws Exception {
        // Initialize the database
        bancoService.save(banco);

        int databaseSizeBeforeDelete = bancoRepository.findAll().size();

        // Get the banco
        restBancoMockMvc.perform(delete("/api/bancos/{id}", banco.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean bancoExistsInEs = bancoSearchRepository.exists(banco.getId());
        assertThat(bancoExistsInEs).isFalse();

        // Validate the database is empty
        List<Banco> bancos = bancoRepository.findAll();
        assertThat(bancos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBanco() throws Exception {
        // Initialize the database
        bancoService.save(banco);

        // Search the banco
        restBancoMockMvc.perform(get("/api/_search/bancos?query=id:" + banco.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(banco.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.toString())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())));
    }
}
