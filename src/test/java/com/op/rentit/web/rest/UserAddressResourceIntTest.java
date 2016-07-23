package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.UserAddress;
import com.op.rentit.repository.UserAddressRepository;
import com.op.rentit.repository.search.UserAddressSearchRepository;

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
 * Test class for the UserAddressResource REST controller.
 *
 * @see UserAddressResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class UserAddressResourceIntTest {

    private static final String DEFAULT_STREET_ADDRESS = "AAAAA";
    private static final String UPDATED_STREET_ADDRESS = "BBBBB";
    private static final String DEFAULT_POSTAL_CODE = "AAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBB";
    private static final String DEFAULT_CITY = "AAAAA";
    private static final String UPDATED_CITY = "BBBBB";
    private static final String DEFAULT_STATE_PROVINCE = "AAAAA";
    private static final String UPDATED_STATE_PROVINCE = "BBBBB";

    @Inject
    private UserAddressRepository userAddressRepository;

    @Inject
    private UserAddressSearchRepository userAddressSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restUserAddressMockMvc;

    private UserAddress userAddress;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserAddressResource userAddressResource = new UserAddressResource();
        ReflectionTestUtils.setField(userAddressResource, "userAddressSearchRepository", userAddressSearchRepository);
        ReflectionTestUtils.setField(userAddressResource, "userAddressRepository", userAddressRepository);
        this.restUserAddressMockMvc = MockMvcBuilders.standaloneSetup(userAddressResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        userAddressSearchRepository.deleteAll();
        userAddress = new UserAddress();
        userAddress.setStreetAddress(DEFAULT_STREET_ADDRESS);
        userAddress.setPostalCode(DEFAULT_POSTAL_CODE);
        userAddress.setCity(DEFAULT_CITY);
        userAddress.setStateProvince(DEFAULT_STATE_PROVINCE);
    }

    @Test
    @Transactional
    public void createUserAddress() throws Exception {
        int databaseSizeBeforeCreate = userAddressRepository.findAll().size();

        // Create the UserAddress

        restUserAddressMockMvc.perform(post("/api/user-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userAddress)))
                .andExpect(status().isCreated());

        // Validate the UserAddress in the database
        List<UserAddress> userAddresses = userAddressRepository.findAll();
        assertThat(userAddresses).hasSize(databaseSizeBeforeCreate + 1);
        UserAddress testUserAddress = userAddresses.get(userAddresses.size() - 1);
        assertThat(testUserAddress.getStreetAddress()).isEqualTo(DEFAULT_STREET_ADDRESS);
        assertThat(testUserAddress.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testUserAddress.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testUserAddress.getStateProvince()).isEqualTo(DEFAULT_STATE_PROVINCE);

        // Validate the UserAddress in ElasticSearch
        UserAddress userAddressEs = userAddressSearchRepository.findOne(testUserAddress.getId());
        assertThat(userAddressEs).isEqualToComparingFieldByField(testUserAddress);
    }

    @Test
    @Transactional
    public void getAllUserAddresses() throws Exception {
        // Initialize the database
        userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddresses
        restUserAddressMockMvc.perform(get("/api/user-addresses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(userAddress.getId().intValue())))
                .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE.toString())))
                .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
                .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE.toString())));
    }

    @Test
    @Transactional
    public void getUserAddress() throws Exception {
        // Initialize the database
        userAddressRepository.saveAndFlush(userAddress);

        // Get the userAddress
        restUserAddressMockMvc.perform(get("/api/user-addresses/{id}", userAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(userAddress.getId().intValue()))
            .andExpect(jsonPath("$.streetAddress").value(DEFAULT_STREET_ADDRESS.toString()))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.stateProvince").value(DEFAULT_STATE_PROVINCE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserAddress() throws Exception {
        // Get the userAddress
        restUserAddressMockMvc.perform(get("/api/user-addresses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserAddress() throws Exception {
        // Initialize the database
        userAddressRepository.saveAndFlush(userAddress);
        userAddressSearchRepository.save(userAddress);
        int databaseSizeBeforeUpdate = userAddressRepository.findAll().size();

        // Update the userAddress
        UserAddress updatedUserAddress = new UserAddress();
        updatedUserAddress.setId(userAddress.getId());
        updatedUserAddress.setStreetAddress(UPDATED_STREET_ADDRESS);
        updatedUserAddress.setPostalCode(UPDATED_POSTAL_CODE);
        updatedUserAddress.setCity(UPDATED_CITY);
        updatedUserAddress.setStateProvince(UPDATED_STATE_PROVINCE);

        restUserAddressMockMvc.perform(put("/api/user-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedUserAddress)))
                .andExpect(status().isOk());

        // Validate the UserAddress in the database
        List<UserAddress> userAddresses = userAddressRepository.findAll();
        assertThat(userAddresses).hasSize(databaseSizeBeforeUpdate);
        UserAddress testUserAddress = userAddresses.get(userAddresses.size() - 1);
        assertThat(testUserAddress.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testUserAddress.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testUserAddress.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testUserAddress.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);

        // Validate the UserAddress in ElasticSearch
        UserAddress userAddressEs = userAddressSearchRepository.findOne(testUserAddress.getId());
        assertThat(userAddressEs).isEqualToComparingFieldByField(testUserAddress);
    }

    @Test
    @Transactional
    public void deleteUserAddress() throws Exception {
        // Initialize the database
        userAddressRepository.saveAndFlush(userAddress);
        userAddressSearchRepository.save(userAddress);
        int databaseSizeBeforeDelete = userAddressRepository.findAll().size();

        // Get the userAddress
        restUserAddressMockMvc.perform(delete("/api/user-addresses/{id}", userAddress.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean userAddressExistsInEs = userAddressSearchRepository.exists(userAddress.getId());
        assertThat(userAddressExistsInEs).isFalse();

        // Validate the database is empty
        List<UserAddress> userAddresses = userAddressRepository.findAll();
        assertThat(userAddresses).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUserAddress() throws Exception {
        // Initialize the database
        userAddressRepository.saveAndFlush(userAddress);
        userAddressSearchRepository.save(userAddress);

        // Search the userAddress
        restUserAddressMockMvc.perform(get("/api/_search/user-addresses?query=id:" + userAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE.toString())));
    }
}
