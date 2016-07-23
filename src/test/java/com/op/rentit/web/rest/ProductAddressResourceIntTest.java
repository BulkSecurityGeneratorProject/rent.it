package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.ProductAddress;
import com.op.rentit.repository.ProductAddressRepository;
import com.op.rentit.repository.search.ProductAddressSearchRepository;

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
 * Test class for the ProductAddressResource REST controller.
 *
 * @see ProductAddressResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class ProductAddressResourceIntTest {


    @Inject
    private ProductAddressRepository productAddressRepository;

    @Inject
    private ProductAddressSearchRepository productAddressSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restProductAddressMockMvc;

    private ProductAddress productAddress;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProductAddressResource productAddressResource = new ProductAddressResource();
        ReflectionTestUtils.setField(productAddressResource, "productAddressSearchRepository", productAddressSearchRepository);
        ReflectionTestUtils.setField(productAddressResource, "productAddressRepository", productAddressRepository);
        this.restProductAddressMockMvc = MockMvcBuilders.standaloneSetup(productAddressResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        productAddressSearchRepository.deleteAll();
        productAddress = new ProductAddress();
    }

    @Test
    @Transactional
    public void createProductAddress() throws Exception {
        int databaseSizeBeforeCreate = productAddressRepository.findAll().size();

        // Create the ProductAddress

        restProductAddressMockMvc.perform(post("/api/product-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productAddress)))
                .andExpect(status().isCreated());

        // Validate the ProductAddress in the database
        List<ProductAddress> productAddresses = productAddressRepository.findAll();
        assertThat(productAddresses).hasSize(databaseSizeBeforeCreate + 1);
        ProductAddress testProductAddress = productAddresses.get(productAddresses.size() - 1);

        // Validate the ProductAddress in ElasticSearch
        ProductAddress productAddressEs = productAddressSearchRepository.findOne(testProductAddress.getId());
        assertThat(productAddressEs).isEqualToComparingFieldByField(testProductAddress);
    }

    @Test
    @Transactional
    public void getAllProductAddresses() throws Exception {
        // Initialize the database
        productAddressRepository.saveAndFlush(productAddress);

        // Get all the productAddresses
        restProductAddressMockMvc.perform(get("/api/product-addresses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(productAddress.getId().intValue())));
    }

    @Test
    @Transactional
    public void getProductAddress() throws Exception {
        // Initialize the database
        productAddressRepository.saveAndFlush(productAddress);

        // Get the productAddress
        restProductAddressMockMvc.perform(get("/api/product-addresses/{id}", productAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(productAddress.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingProductAddress() throws Exception {
        // Get the productAddress
        restProductAddressMockMvc.perform(get("/api/product-addresses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductAddress() throws Exception {
        // Initialize the database
        productAddressRepository.saveAndFlush(productAddress);
        productAddressSearchRepository.save(productAddress);
        int databaseSizeBeforeUpdate = productAddressRepository.findAll().size();

        // Update the productAddress
        ProductAddress updatedProductAddress = new ProductAddress();
        updatedProductAddress.setId(productAddress.getId());

        restProductAddressMockMvc.perform(put("/api/product-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedProductAddress)))
                .andExpect(status().isOk());

        // Validate the ProductAddress in the database
        List<ProductAddress> productAddresses = productAddressRepository.findAll();
        assertThat(productAddresses).hasSize(databaseSizeBeforeUpdate);
        ProductAddress testProductAddress = productAddresses.get(productAddresses.size() - 1);

        // Validate the ProductAddress in ElasticSearch
        ProductAddress productAddressEs = productAddressSearchRepository.findOne(testProductAddress.getId());
        assertThat(productAddressEs).isEqualToComparingFieldByField(testProductAddress);
    }

    @Test
    @Transactional
    public void deleteProductAddress() throws Exception {
        // Initialize the database
        productAddressRepository.saveAndFlush(productAddress);
        productAddressSearchRepository.save(productAddress);
        int databaseSizeBeforeDelete = productAddressRepository.findAll().size();

        // Get the productAddress
        restProductAddressMockMvc.perform(delete("/api/product-addresses/{id}", productAddress.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean productAddressExistsInEs = productAddressSearchRepository.exists(productAddress.getId());
        assertThat(productAddressExistsInEs).isFalse();

        // Validate the database is empty
        List<ProductAddress> productAddresses = productAddressRepository.findAll();
        assertThat(productAddresses).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProductAddress() throws Exception {
        // Initialize the database
        productAddressRepository.saveAndFlush(productAddress);
        productAddressSearchRepository.save(productAddress);

        // Search the productAddress
        restProductAddressMockMvc.perform(get("/api/_search/product-addresses?query=id:" + productAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productAddress.getId().intValue())));
    }
}
