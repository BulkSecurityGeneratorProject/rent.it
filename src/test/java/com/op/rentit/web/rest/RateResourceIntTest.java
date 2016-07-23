package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.Rate;
import com.op.rentit.repository.RateRepository;
import com.op.rentit.repository.search.RateSearchRepository;

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
import java.math.BigDecimal;;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the RateResource REST controller.
 *
 * @see RateResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class RateResourceIntTest {


    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private RateRepository rateRepository;

    @Inject
    private RateSearchRepository rateSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRateMockMvc;

    private Rate rate;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RateResource rateResource = new RateResource();
        ReflectionTestUtils.setField(rateResource, "rateSearchRepository", rateSearchRepository);
        ReflectionTestUtils.setField(rateResource, "rateRepository", rateRepository);
        this.restRateMockMvc = MockMvcBuilders.standaloneSetup(rateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        rateSearchRepository.deleteAll();
        rate = new Rate();
        rate.setPrice(DEFAULT_PRICE);
        rate.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createRate() throws Exception {
        int databaseSizeBeforeCreate = rateRepository.findAll().size();

        // Create the Rate

        restRateMockMvc.perform(post("/api/rates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rate)))
                .andExpect(status().isCreated());

        // Validate the Rate in the database
        List<Rate> rates = rateRepository.findAll();
        assertThat(rates).hasSize(databaseSizeBeforeCreate + 1);
        Rate testRate = rates.get(rates.size() - 1);
        assertThat(testRate.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testRate.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Rate in ElasticSearch
        Rate rateEs = rateSearchRepository.findOne(testRate.getId());
        assertThat(rateEs).isEqualToComparingFieldByField(testRate);
    }

    @Test
    @Transactional
    public void getAllRates() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        // Get all the rates
        restRateMockMvc.perform(get("/api/rates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rate.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        // Get the rate
        restRateMockMvc.perform(get("/api/rates/{id}", rate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(rate.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRate() throws Exception {
        // Get the rate
        restRateMockMvc.perform(get("/api/rates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);
        rateSearchRepository.save(rate);
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();

        // Update the rate
        Rate updatedRate = new Rate();
        updatedRate.setId(rate.getId());
        updatedRate.setPrice(UPDATED_PRICE);
        updatedRate.setName(UPDATED_NAME);

        restRateMockMvc.perform(put("/api/rates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRate)))
                .andExpect(status().isOk());

        // Validate the Rate in the database
        List<Rate> rates = rateRepository.findAll();
        assertThat(rates).hasSize(databaseSizeBeforeUpdate);
        Rate testRate = rates.get(rates.size() - 1);
        assertThat(testRate.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testRate.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Rate in ElasticSearch
        Rate rateEs = rateSearchRepository.findOne(testRate.getId());
        assertThat(rateEs).isEqualToComparingFieldByField(testRate);
    }

    @Test
    @Transactional
    public void deleteRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);
        rateSearchRepository.save(rate);
        int databaseSizeBeforeDelete = rateRepository.findAll().size();

        // Get the rate
        restRateMockMvc.perform(delete("/api/rates/{id}", rate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean rateExistsInEs = rateSearchRepository.exists(rate.getId());
        assertThat(rateExistsInEs).isFalse();

        // Validate the database is empty
        List<Rate> rates = rateRepository.findAll();
        assertThat(rates).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);
        rateSearchRepository.save(rate);

        // Search the rate
        restRateMockMvc.perform(get("/api/_search/rates?query=id:" + rate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rate.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
