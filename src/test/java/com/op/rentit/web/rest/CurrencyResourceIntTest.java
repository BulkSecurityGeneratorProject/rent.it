package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.Currency;
import com.op.rentit.repository.CurrencyRepository;
import com.op.rentit.repository.search.CurrencySearchRepository;

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
 * Test class for the CurrencyResource REST controller.
 *
 * @see CurrencyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class CurrencyResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_CODE = "AAAAA";
    private static final String UPDATED_CODE = "BBBBB";
    private static final String DEFAULT_SYMBOL = "AAAAA";
    private static final String UPDATED_SYMBOL = "BBBBB";

    @Inject
    private CurrencyRepository currencyRepository;

    @Inject
    private CurrencySearchRepository currencySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCurrencyMockMvc;

    private Currency currency;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CurrencyResource currencyResource = new CurrencyResource();
        ReflectionTestUtils.setField(currencyResource, "currencySearchRepository", currencySearchRepository);
        ReflectionTestUtils.setField(currencyResource, "currencyRepository", currencyRepository);
        this.restCurrencyMockMvc = MockMvcBuilders.standaloneSetup(currencyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        currencySearchRepository.deleteAll();
        currency = new Currency();
        currency.setName(DEFAULT_NAME);
        currency.setCode(DEFAULT_CODE);
        currency.setSymbol(DEFAULT_SYMBOL);
    }

    @Test
    @Transactional
    public void createCurrency() throws Exception {
        int databaseSizeBeforeCreate = currencyRepository.findAll().size();

        // Create the Currency

        restCurrencyMockMvc.perform(post("/api/currencies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(currency)))
                .andExpect(status().isCreated());

        // Validate the Currency in the database
        List<Currency> currencies = currencyRepository.findAll();
        assertThat(currencies).hasSize(databaseSizeBeforeCreate + 1);
        Currency testCurrency = currencies.get(currencies.size() - 1);
        assertThat(testCurrency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCurrency.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCurrency.getSymbol()).isEqualTo(DEFAULT_SYMBOL);

        // Validate the Currency in ElasticSearch
        Currency currencyEs = currencySearchRepository.findOne(testCurrency.getId());
        assertThat(currencyEs).isEqualToComparingFieldByField(testCurrency);
    }

    @Test
    @Transactional
    public void getAllCurrencies() throws Exception {
        // Initialize the database
        currencyRepository.saveAndFlush(currency);

        // Get all the currencies
        restCurrencyMockMvc.perform(get("/api/currencies?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(currency.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
                .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())));
    }

    @Test
    @Transactional
    public void getCurrency() throws Exception {
        // Initialize the database
        currencyRepository.saveAndFlush(currency);

        // Get the currency
        restCurrencyMockMvc.perform(get("/api/currencies/{id}", currency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(currency.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCurrency() throws Exception {
        // Get the currency
        restCurrencyMockMvc.perform(get("/api/currencies/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCurrency() throws Exception {
        // Initialize the database
        currencyRepository.saveAndFlush(currency);
        currencySearchRepository.save(currency);
        int databaseSizeBeforeUpdate = currencyRepository.findAll().size();

        // Update the currency
        Currency updatedCurrency = new Currency();
        updatedCurrency.setId(currency.getId());
        updatedCurrency.setName(UPDATED_NAME);
        updatedCurrency.setCode(UPDATED_CODE);
        updatedCurrency.setSymbol(UPDATED_SYMBOL);

        restCurrencyMockMvc.perform(put("/api/currencies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCurrency)))
                .andExpect(status().isOk());

        // Validate the Currency in the database
        List<Currency> currencies = currencyRepository.findAll();
        assertThat(currencies).hasSize(databaseSizeBeforeUpdate);
        Currency testCurrency = currencies.get(currencies.size() - 1);
        assertThat(testCurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCurrency.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCurrency.getSymbol()).isEqualTo(UPDATED_SYMBOL);

        // Validate the Currency in ElasticSearch
        Currency currencyEs = currencySearchRepository.findOne(testCurrency.getId());
        assertThat(currencyEs).isEqualToComparingFieldByField(testCurrency);
    }

    @Test
    @Transactional
    public void deleteCurrency() throws Exception {
        // Initialize the database
        currencyRepository.saveAndFlush(currency);
        currencySearchRepository.save(currency);
        int databaseSizeBeforeDelete = currencyRepository.findAll().size();

        // Get the currency
        restCurrencyMockMvc.perform(delete("/api/currencies/{id}", currency.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean currencyExistsInEs = currencySearchRepository.exists(currency.getId());
        assertThat(currencyExistsInEs).isFalse();

        // Validate the database is empty
        List<Currency> currencies = currencyRepository.findAll();
        assertThat(currencies).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCurrency() throws Exception {
        // Initialize the database
        currencyRepository.saveAndFlush(currency);
        currencySearchRepository.save(currency);

        // Search the currency
        restCurrencyMockMvc.perform(get("/api/_search/currencies?query=id:" + currency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(currency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())));
    }
}
