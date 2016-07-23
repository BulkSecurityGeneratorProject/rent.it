package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.TimeSlot;
import com.op.rentit.repository.TimeSlotRepository;
import com.op.rentit.repository.search.TimeSlotSearchRepository;

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
 * Test class for the TimeSlotResource REST controller.
 *
 * @see TimeSlotResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class TimeSlotResourceIntTest {

    private static final String DEFAULT_DATE_TIME = "AAAAA";
    private static final String UPDATED_DATE_TIME = "BBBBB";

    private static final Long DEFAULT_DURATION = 1L;
    private static final Long UPDATED_DURATION = 2L;

    @Inject
    private TimeSlotRepository timeSlotRepository;

    @Inject
    private TimeSlotSearchRepository timeSlotSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTimeSlotMockMvc;

    private TimeSlot timeSlot;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TimeSlotResource timeSlotResource = new TimeSlotResource();
        ReflectionTestUtils.setField(timeSlotResource, "timeSlotSearchRepository", timeSlotSearchRepository);
        ReflectionTestUtils.setField(timeSlotResource, "timeSlotRepository", timeSlotRepository);
        this.restTimeSlotMockMvc = MockMvcBuilders.standaloneSetup(timeSlotResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        timeSlotSearchRepository.deleteAll();
        timeSlot = new TimeSlot();
        timeSlot.setDateTime(DEFAULT_DATE_TIME);
        timeSlot.setDuration(DEFAULT_DURATION);
    }

    @Test
    @Transactional
    public void createTimeSlot() throws Exception {
        int databaseSizeBeforeCreate = timeSlotRepository.findAll().size();

        // Create the TimeSlot

        restTimeSlotMockMvc.perform(post("/api/time-slots")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeSlot)))
                .andExpect(status().isCreated());

        // Validate the TimeSlot in the database
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeCreate + 1);
        TimeSlot testTimeSlot = timeSlots.get(timeSlots.size() - 1);
        assertThat(testTimeSlot.getDateTime()).isEqualTo(DEFAULT_DATE_TIME);
        assertThat(testTimeSlot.getDuration()).isEqualTo(DEFAULT_DURATION);

        // Validate the TimeSlot in ElasticSearch
        TimeSlot timeSlotEs = timeSlotSearchRepository.findOne(testTimeSlot.getId());
        assertThat(timeSlotEs).isEqualToComparingFieldByField(testTimeSlot);
    }

    @Test
    @Transactional
    public void getAllTimeSlots() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);

        // Get all the timeSlots
        restTimeSlotMockMvc.perform(get("/api/time-slots?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(timeSlot.getId().intValue())))
                .andExpect(jsonPath("$.[*].dateTime").value(hasItem(DEFAULT_DATE_TIME.toString())))
                .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.intValue())));
    }

    @Test
    @Transactional
    public void getTimeSlot() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);

        // Get the timeSlot
        restTimeSlotMockMvc.perform(get("/api/time-slots/{id}", timeSlot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(timeSlot.getId().intValue()))
            .andExpect(jsonPath("$.dateTime").value(DEFAULT_DATE_TIME.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTimeSlot() throws Exception {
        // Get the timeSlot
        restTimeSlotMockMvc.perform(get("/api/time-slots/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTimeSlot() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);
        timeSlotSearchRepository.save(timeSlot);
        int databaseSizeBeforeUpdate = timeSlotRepository.findAll().size();

        // Update the timeSlot
        TimeSlot updatedTimeSlot = new TimeSlot();
        updatedTimeSlot.setId(timeSlot.getId());
        updatedTimeSlot.setDateTime(UPDATED_DATE_TIME);
        updatedTimeSlot.setDuration(UPDATED_DURATION);

        restTimeSlotMockMvc.perform(put("/api/time-slots")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTimeSlot)))
                .andExpect(status().isOk());

        // Validate the TimeSlot in the database
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeUpdate);
        TimeSlot testTimeSlot = timeSlots.get(timeSlots.size() - 1);
        assertThat(testTimeSlot.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testTimeSlot.getDuration()).isEqualTo(UPDATED_DURATION);

        // Validate the TimeSlot in ElasticSearch
        TimeSlot timeSlotEs = timeSlotSearchRepository.findOne(testTimeSlot.getId());
        assertThat(timeSlotEs).isEqualToComparingFieldByField(testTimeSlot);
    }

    @Test
    @Transactional
    public void deleteTimeSlot() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);
        timeSlotSearchRepository.save(timeSlot);
        int databaseSizeBeforeDelete = timeSlotRepository.findAll().size();

        // Get the timeSlot
        restTimeSlotMockMvc.perform(delete("/api/time-slots/{id}", timeSlot.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean timeSlotExistsInEs = timeSlotSearchRepository.exists(timeSlot.getId());
        assertThat(timeSlotExistsInEs).isFalse();

        // Validate the database is empty
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTimeSlot() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);
        timeSlotSearchRepository.save(timeSlot);

        // Search the timeSlot
        restTimeSlotMockMvc.perform(get("/api/_search/time-slots?query=id:" + timeSlot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeSlot.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateTime").value(hasItem(DEFAULT_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.intValue())));
    }
}
