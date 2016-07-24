package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.AppSettings;
import com.op.rentit.repository.AppSettingsRepository;
import com.op.rentit.repository.search.AppSettingsSearchRepository;

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
 * Test class for the AppSettingsResource REST controller.
 *
 * @see AppSettingsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class AppSettingsResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VALUE = "AAAAA";
    private static final String UPDATED_VALUE = "BBBBB";
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private AppSettingsRepository appSettingsRepository;

    @Inject
    private AppSettingsSearchRepository appSettingsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAppSettingsMockMvc;

    private AppSettings appSettings;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AppSettingsResource appSettingsResource = new AppSettingsResource();
        ReflectionTestUtils.setField(appSettingsResource, "appSettingsSearchRepository", appSettingsSearchRepository);
        ReflectionTestUtils.setField(appSettingsResource, "appSettingsRepository", appSettingsRepository);
        this.restAppSettingsMockMvc = MockMvcBuilders.standaloneSetup(appSettingsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        appSettingsSearchRepository.deleteAll();
        appSettings = new AppSettings();
        appSettings.setName(DEFAULT_NAME);
        appSettings.setValue(DEFAULT_VALUE);
        appSettings.setType(DEFAULT_TYPE);
        appSettings.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createAppSettings() throws Exception {
        int databaseSizeBeforeCreate = appSettingsRepository.findAll().size();

        // Create the AppSettings

        restAppSettingsMockMvc.perform(post("/api/app-settings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appSettings)))
                .andExpect(status().isCreated());

        // Validate the AppSettings in the database
        List<AppSettings> appSettings = appSettingsRepository.findAll();
        assertThat(appSettings).hasSize(databaseSizeBeforeCreate + 1);
        AppSettings testAppSettings = appSettings.get(appSettings.size() - 1);
        assertThat(testAppSettings.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppSettings.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testAppSettings.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testAppSettings.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AppSettings in ElasticSearch
        AppSettings appSettingsEs = appSettingsSearchRepository.findOne(testAppSettings.getId());
        assertThat(appSettingsEs).isEqualToComparingFieldByField(testAppSettings);
    }

    @Test
    @Transactional
    public void getAllAppSettings() throws Exception {
        // Initialize the database
        appSettingsRepository.saveAndFlush(appSettings);

        // Get all the appSettings
        restAppSettingsMockMvc.perform(get("/api/app-settings?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(appSettings.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getAppSettings() throws Exception {
        // Initialize the database
        appSettingsRepository.saveAndFlush(appSettings);

        // Get the appSettings
        restAppSettingsMockMvc.perform(get("/api/app-settings/{id}", appSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(appSettings.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAppSettings() throws Exception {
        // Get the appSettings
        restAppSettingsMockMvc.perform(get("/api/app-settings/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppSettings() throws Exception {
        // Initialize the database
        appSettingsRepository.saveAndFlush(appSettings);
        appSettingsSearchRepository.save(appSettings);
        int databaseSizeBeforeUpdate = appSettingsRepository.findAll().size();

        // Update the appSettings
        AppSettings updatedAppSettings = new AppSettings();
        updatedAppSettings.setId(appSettings.getId());
        updatedAppSettings.setName(UPDATED_NAME);
        updatedAppSettings.setValue(UPDATED_VALUE);
        updatedAppSettings.setType(UPDATED_TYPE);
        updatedAppSettings.setDescription(UPDATED_DESCRIPTION);

        restAppSettingsMockMvc.perform(put("/api/app-settings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAppSettings)))
                .andExpect(status().isOk());

        // Validate the AppSettings in the database
        List<AppSettings> appSettings = appSettingsRepository.findAll();
        assertThat(appSettings).hasSize(databaseSizeBeforeUpdate);
        AppSettings testAppSettings = appSettings.get(appSettings.size() - 1);
        assertThat(testAppSettings.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppSettings.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testAppSettings.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testAppSettings.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AppSettings in ElasticSearch
        AppSettings appSettingsEs = appSettingsSearchRepository.findOne(testAppSettings.getId());
        assertThat(appSettingsEs).isEqualToComparingFieldByField(testAppSettings);
    }

    @Test
    @Transactional
    public void deleteAppSettings() throws Exception {
        // Initialize the database
        appSettingsRepository.saveAndFlush(appSettings);
        appSettingsSearchRepository.save(appSettings);
        int databaseSizeBeforeDelete = appSettingsRepository.findAll().size();

        // Get the appSettings
        restAppSettingsMockMvc.perform(delete("/api/app-settings/{id}", appSettings.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean appSettingsExistsInEs = appSettingsSearchRepository.exists(appSettings.getId());
        assertThat(appSettingsExistsInEs).isFalse();

        // Validate the database is empty
        List<AppSettings> appSettings = appSettingsRepository.findAll();
        assertThat(appSettings).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAppSettings() throws Exception {
        // Initialize the database
        appSettingsRepository.saveAndFlush(appSettings);
        appSettingsSearchRepository.save(appSettings);

        // Search the appSettings
        restAppSettingsMockMvc.perform(get("/api/_search/app-settings?query=id:" + appSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appSettings.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
