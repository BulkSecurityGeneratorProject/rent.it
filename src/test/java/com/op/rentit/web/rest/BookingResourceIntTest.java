package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.Booking;
import com.op.rentit.repository.BookingRepository;
import com.op.rentit.repository.search.BookingSearchRepository;

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
 * Test class for the BookingResource REST controller.
 *
 * @see BookingResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class BookingResourceIntTest {

    private static final String DEFAULT_DATE_TIME = "AAAAA";
    private static final String UPDATED_DATE_TIME = "BBBBB";
    private static final String DEFAULT_PAYMENT_TYPE = "AAAAA";
    private static final String UPDATED_PAYMENT_TYPE = "BBBBB";

    @Inject
    private BookingRepository bookingRepository;

    @Inject
    private BookingSearchRepository bookingSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBookingMockMvc;

    private Booking booking;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BookingResource bookingResource = new BookingResource();
        ReflectionTestUtils.setField(bookingResource, "bookingSearchRepository", bookingSearchRepository);
        ReflectionTestUtils.setField(bookingResource, "bookingRepository", bookingRepository);
        this.restBookingMockMvc = MockMvcBuilders.standaloneSetup(bookingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        bookingSearchRepository.deleteAll();
        booking = new Booking();
        booking.setDateTime(DEFAULT_DATE_TIME);
        booking.setPaymentType(DEFAULT_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    public void createBooking() throws Exception {
        int databaseSizeBeforeCreate = bookingRepository.findAll().size();

        // Create the Booking

        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(booking)))
                .andExpect(status().isCreated());

        // Validate the Booking in the database
        List<Booking> bookings = bookingRepository.findAll();
        assertThat(bookings).hasSize(databaseSizeBeforeCreate + 1);
        Booking testBooking = bookings.get(bookings.size() - 1);
        assertThat(testBooking.getDateTime()).isEqualTo(DEFAULT_DATE_TIME);
        assertThat(testBooking.getPaymentType()).isEqualTo(DEFAULT_PAYMENT_TYPE);

        // Validate the Booking in ElasticSearch
        Booking bookingEs = bookingSearchRepository.findOne(testBooking.getId());
        assertThat(bookingEs).isEqualToComparingFieldByField(testBooking);
    }

    @Test
    @Transactional
    public void getAllBookings() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookings
        restBookingMockMvc.perform(get("/api/bookings?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(booking.getId().intValue())))
                .andExpect(jsonPath("$.[*].dateTime").value(hasItem(DEFAULT_DATE_TIME.toString())))
                .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get the booking
        restBookingMockMvc.perform(get("/api/bookings/{id}", booking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(booking.getId().intValue()))
            .andExpect(jsonPath("$.dateTime").value(DEFAULT_DATE_TIME.toString()))
            .andExpect(jsonPath("$.paymentType").value(DEFAULT_PAYMENT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBooking() throws Exception {
        // Get the booking
        restBookingMockMvc.perform(get("/api/bookings/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);
        bookingSearchRepository.save(booking);
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking
        Booking updatedBooking = new Booking();
        updatedBooking.setId(booking.getId());
        updatedBooking.setDateTime(UPDATED_DATE_TIME);
        updatedBooking.setPaymentType(UPDATED_PAYMENT_TYPE);

        restBookingMockMvc.perform(put("/api/bookings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBooking)))
                .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookings = bookingRepository.findAll();
        assertThat(bookings).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookings.get(bookings.size() - 1);
        assertThat(testBooking.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testBooking.getPaymentType()).isEqualTo(UPDATED_PAYMENT_TYPE);

        // Validate the Booking in ElasticSearch
        Booking bookingEs = bookingSearchRepository.findOne(testBooking.getId());
        assertThat(bookingEs).isEqualToComparingFieldByField(testBooking);
    }

    @Test
    @Transactional
    public void deleteBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);
        bookingSearchRepository.save(booking);
        int databaseSizeBeforeDelete = bookingRepository.findAll().size();

        // Get the booking
        restBookingMockMvc.perform(delete("/api/bookings/{id}", booking.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean bookingExistsInEs = bookingSearchRepository.exists(booking.getId());
        assertThat(bookingExistsInEs).isFalse();

        // Validate the database is empty
        List<Booking> bookings = bookingRepository.findAll();
        assertThat(bookings).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);
        bookingSearchRepository.save(booking);

        // Search the booking
        restBookingMockMvc.perform(get("/api/_search/bookings?query=id:" + booking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(booking.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateTime").value(hasItem(DEFAULT_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE.toString())));
    }
}
