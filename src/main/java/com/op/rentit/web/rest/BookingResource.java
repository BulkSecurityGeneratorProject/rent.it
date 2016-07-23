package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.Booking;
import com.op.rentit.repository.BookingRepository;
import com.op.rentit.repository.search.BookingSearchRepository;
import com.op.rentit.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Booking.
 */
@RestController
@RequestMapping("/api")
public class BookingResource {

    private final Logger log = LoggerFactory.getLogger(BookingResource.class);
        
    @Inject
    private BookingRepository bookingRepository;
    
    @Inject
    private BookingSearchRepository bookingSearchRepository;
    
    /**
     * POST  /bookings : Create a new booking.
     *
     * @param booking the booking to create
     * @return the ResponseEntity with status 201 (Created) and with body the new booking, or with status 400 (Bad Request) if the booking has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bookings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) throws URISyntaxException {
        log.debug("REST request to save Booking : {}", booking);
        if (booking.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("booking", "idexists", "A new booking cannot already have an ID")).body(null);
        }
        Booking result = bookingRepository.save(booking);
        bookingSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/bookings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("booking", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookings : Updates an existing booking.
     *
     * @param booking the booking to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated booking,
     * or with status 400 (Bad Request) if the booking is not valid,
     * or with status 500 (Internal Server Error) if the booking couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bookings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) throws URISyntaxException {
        log.debug("REST request to update Booking : {}", booking);
        if (booking.getId() == null) {
            return createBooking(booking);
        }
        Booking result = bookingRepository.save(booking);
        bookingSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("booking", booking.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bookings : get all the bookings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of bookings in body
     */
    @RequestMapping(value = "/bookings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Booking> getAllBookings() {
        log.debug("REST request to get all Bookings");
        List<Booking> bookings = bookingRepository.findAll();
        return bookings;
    }

    /**
     * GET  /bookings/:id : get the "id" booking.
     *
     * @param id the id of the booking to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the booking, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/bookings/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        log.debug("REST request to get Booking : {}", id);
        Booking booking = bookingRepository.findOne(id);
        return Optional.ofNullable(booking)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bookings/:id : delete the "id" booking.
     *
     * @param id the id of the booking to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/bookings/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        log.debug("REST request to delete Booking : {}", id);
        bookingRepository.delete(id);
        bookingSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("booking", id.toString())).build();
    }

    /**
     * SEARCH  /_search/bookings?query=:query : search for the booking corresponding
     * to the query.
     *
     * @param query the query of the booking search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/bookings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Booking> searchBookings(@RequestParam String query) {
        log.debug("REST request to search Bookings for query {}", query);
        return StreamSupport
            .stream(bookingSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
