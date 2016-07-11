package br.org.rh.web.rest;

import br.org.rh.RhApp;
import br.org.rh.domain.Vinculo;
import br.org.rh.repository.VinculoRepository;
import br.org.rh.service.VinculoService;
import br.org.rh.repository.search.VinculoSearchRepository;

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
 * Test class for the VinculoResource REST controller.
 *
 * @see VinculoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RhApp.class)
@WebAppConfiguration
@IntegrationTest
public class VinculoResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";
    private static final String DEFAULT_DESCRICAO = "AAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBB";

    @Inject
    private VinculoRepository vinculoRepository;

    @Inject
    private VinculoService vinculoService;

    @Inject
    private VinculoSearchRepository vinculoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restVinculoMockMvc;

    private Vinculo vinculo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VinculoResource vinculoResource = new VinculoResource();
        ReflectionTestUtils.setField(vinculoResource, "vinculoService", vinculoService);
        this.restVinculoMockMvc = MockMvcBuilders.standaloneSetup(vinculoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        vinculoSearchRepository.deleteAll();
        vinculo = new Vinculo();
        vinculo.setNome(DEFAULT_NOME);
        vinculo.setDescricao(DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    public void createVinculo() throws Exception {
        int databaseSizeBeforeCreate = vinculoRepository.findAll().size();

        // Create the Vinculo

        restVinculoMockMvc.perform(post("/api/vinculos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(vinculo)))
                .andExpect(status().isCreated());

        // Validate the Vinculo in the database
        List<Vinculo> vinculos = vinculoRepository.findAll();
        assertThat(vinculos).hasSize(databaseSizeBeforeCreate + 1);
        Vinculo testVinculo = vinculos.get(vinculos.size() - 1);
        assertThat(testVinculo.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testVinculo.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);

        // Validate the Vinculo in ElasticSearch
        Vinculo vinculoEs = vinculoSearchRepository.findOne(testVinculo.getId());
        assertThat(vinculoEs).isEqualToComparingFieldByField(testVinculo);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = vinculoRepository.findAll().size();
        // set the field null
        vinculo.setNome(null);

        // Create the Vinculo, which fails.

        restVinculoMockMvc.perform(post("/api/vinculos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(vinculo)))
                .andExpect(status().isBadRequest());

        List<Vinculo> vinculos = vinculoRepository.findAll();
        assertThat(vinculos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVinculos() throws Exception {
        // Initialize the database
        vinculoRepository.saveAndFlush(vinculo);

        // Get all the vinculos
        restVinculoMockMvc.perform(get("/api/vinculos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(vinculo.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void getVinculo() throws Exception {
        // Initialize the database
        vinculoRepository.saveAndFlush(vinculo);

        // Get the vinculo
        restVinculoMockMvc.perform(get("/api/vinculos/{id}", vinculo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(vinculo.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingVinculo() throws Exception {
        // Get the vinculo
        restVinculoMockMvc.perform(get("/api/vinculos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVinculo() throws Exception {
        // Initialize the database
        vinculoService.save(vinculo);

        int databaseSizeBeforeUpdate = vinculoRepository.findAll().size();

        // Update the vinculo
        Vinculo updatedVinculo = new Vinculo();
        updatedVinculo.setId(vinculo.getId());
        updatedVinculo.setNome(UPDATED_NOME);
        updatedVinculo.setDescricao(UPDATED_DESCRICAO);

        restVinculoMockMvc.perform(put("/api/vinculos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedVinculo)))
                .andExpect(status().isOk());

        // Validate the Vinculo in the database
        List<Vinculo> vinculos = vinculoRepository.findAll();
        assertThat(vinculos).hasSize(databaseSizeBeforeUpdate);
        Vinculo testVinculo = vinculos.get(vinculos.size() - 1);
        assertThat(testVinculo.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testVinculo.getDescricao()).isEqualTo(UPDATED_DESCRICAO);

        // Validate the Vinculo in ElasticSearch
        Vinculo vinculoEs = vinculoSearchRepository.findOne(testVinculo.getId());
        assertThat(vinculoEs).isEqualToComparingFieldByField(testVinculo);
    }

    @Test
    @Transactional
    public void deleteVinculo() throws Exception {
        // Initialize the database
        vinculoService.save(vinculo);

        int databaseSizeBeforeDelete = vinculoRepository.findAll().size();

        // Get the vinculo
        restVinculoMockMvc.perform(delete("/api/vinculos/{id}", vinculo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean vinculoExistsInEs = vinculoSearchRepository.exists(vinculo.getId());
        assertThat(vinculoExistsInEs).isFalse();

        // Validate the database is empty
        List<Vinculo> vinculos = vinculoRepository.findAll();
        assertThat(vinculos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchVinculo() throws Exception {
        // Initialize the database
        vinculoService.save(vinculo);

        // Search the vinculo
        restVinculoMockMvc.perform(get("/api/_search/vinculos?query=id:" + vinculo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vinculo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }
}
