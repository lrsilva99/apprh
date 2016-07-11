package br.org.rh.web.rest;

import br.org.rh.RhApp;
import br.org.rh.domain.Cargo;
import br.org.rh.repository.CargoRepository;
import br.org.rh.service.CargoService;
import br.org.rh.repository.search.CargoSearchRepository;

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
 * Test class for the CargoResource REST controller.
 *
 * @see CargoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RhApp.class)
@WebAppConfiguration
@IntegrationTest
public class CargoResourceIntTest {

    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";
    private static final String DEFAULT_DESCRICAO = "AAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBB";

    @Inject
    private CargoRepository cargoRepository;

    @Inject
    private CargoService cargoService;

    @Inject
    private CargoSearchRepository cargoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCargoMockMvc;

    private Cargo cargo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CargoResource cargoResource = new CargoResource();
        ReflectionTestUtils.setField(cargoResource, "cargoService", cargoService);
        this.restCargoMockMvc = MockMvcBuilders.standaloneSetup(cargoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        cargoSearchRepository.deleteAll();
        cargo = new Cargo();
        cargo.setNome(DEFAULT_NOME);
        cargo.setDescricao(DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    public void createCargo() throws Exception {
        int databaseSizeBeforeCreate = cargoRepository.findAll().size();

        // Create the Cargo

        restCargoMockMvc.perform(post("/api/cargos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cargo)))
                .andExpect(status().isCreated());

        // Validate the Cargo in the database
        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeCreate + 1);
        Cargo testCargo = cargos.get(cargos.size() - 1);
        assertThat(testCargo.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testCargo.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);

        // Validate the Cargo in ElasticSearch
        Cargo cargoEs = cargoSearchRepository.findOne(testCargo.getId());
        assertThat(cargoEs).isEqualToComparingFieldByField(testCargo);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = cargoRepository.findAll().size();
        // set the field null
        cargo.setNome(null);

        // Create the Cargo, which fails.

        restCargoMockMvc.perform(post("/api/cargos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cargo)))
                .andExpect(status().isBadRequest());

        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescricaoIsRequired() throws Exception {
        int databaseSizeBeforeTest = cargoRepository.findAll().size();
        // set the field null
        cargo.setDescricao(null);

        // Create the Cargo, which fails.

        restCargoMockMvc.perform(post("/api/cargos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cargo)))
                .andExpect(status().isBadRequest());

        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCargos() throws Exception {
        // Initialize the database
        cargoRepository.saveAndFlush(cargo);

        // Get all the cargos
        restCargoMockMvc.perform(get("/api/cargos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cargo.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void getCargo() throws Exception {
        // Initialize the database
        cargoRepository.saveAndFlush(cargo);

        // Get the cargo
        restCargoMockMvc.perform(get("/api/cargos/{id}", cargo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cargo.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCargo() throws Exception {
        // Get the cargo
        restCargoMockMvc.perform(get("/api/cargos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCargo() throws Exception {
        // Initialize the database
        cargoService.save(cargo);

        int databaseSizeBeforeUpdate = cargoRepository.findAll().size();

        // Update the cargo
        Cargo updatedCargo = new Cargo();
        updatedCargo.setId(cargo.getId());
        updatedCargo.setNome(UPDATED_NOME);
        updatedCargo.setDescricao(UPDATED_DESCRICAO);

        restCargoMockMvc.perform(put("/api/cargos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCargo)))
                .andExpect(status().isOk());

        // Validate the Cargo in the database
        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeUpdate);
        Cargo testCargo = cargos.get(cargos.size() - 1);
        assertThat(testCargo.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testCargo.getDescricao()).isEqualTo(UPDATED_DESCRICAO);

        // Validate the Cargo in ElasticSearch
        Cargo cargoEs = cargoSearchRepository.findOne(testCargo.getId());
        assertThat(cargoEs).isEqualToComparingFieldByField(testCargo);
    }

    @Test
    @Transactional
    public void deleteCargo() throws Exception {
        // Initialize the database
        cargoService.save(cargo);

        int databaseSizeBeforeDelete = cargoRepository.findAll().size();

        // Get the cargo
        restCargoMockMvc.perform(delete("/api/cargos/{id}", cargo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean cargoExistsInEs = cargoSearchRepository.exists(cargo.getId());
        assertThat(cargoExistsInEs).isFalse();

        // Validate the database is empty
        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCargo() throws Exception {
        // Initialize the database
        cargoService.save(cargo);

        // Search the cargo
        restCargoMockMvc.perform(get("/api/_search/cargos?query=id:" + cargo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cargo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }
}
