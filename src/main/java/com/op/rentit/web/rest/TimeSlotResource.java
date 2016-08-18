package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.TimeSlot;
import com.op.rentit.repository.TimeSlotRepository;
import com.op.rentit.repository.search.TimeSlotSearchRepository;
import com.op.rentit.web.rest.util.HeaderUtil;
import lombok.extern.slf4j.Slf4j;
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
 * REST controller for managing TimeSlot.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class TimeSlotResource {

    @Inject
    private TimeSlotRepository timeSlotRepository;

    @Inject
    private TimeSlotSearchRepository timeSlotSearchRepository;

    /**
     * POST  /time-slots : Create a new timeSlot.
     *
     * @param timeSlot the timeSlot to create
     * @return the ResponseEntity with status 201 (Created) and with body the new timeSlot, or with status 400 (Bad Request) if the timeSlot has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/time-slots",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TimeSlot> createTimeSlot(@RequestBody TimeSlot timeSlot) throws URISyntaxException {
        log.debug("REST request to save TimeSlot : {}", timeSlot);
        if (timeSlot.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("timeSlot", "idexists", "A new timeSlot cannot already have an ID")).body(null);
        }
        TimeSlot result = timeSlotRepository.save(timeSlot);
        timeSlotSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/time-slots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("timeSlot", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /time-slots : Updates an existing timeSlot.
     *
     * @param timeSlot the timeSlot to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated timeSlot,
     * or with status 400 (Bad Request) if the timeSlot is not valid,
     * or with status 500 (Internal Server Error) if the timeSlot couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/time-slots",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TimeSlot> updateTimeSlot(@RequestBody TimeSlot timeSlot) throws URISyntaxException {
        log.debug("REST request to update TimeSlot : {}", timeSlot);
        if (timeSlot.getId() == null) {
            return createTimeSlot(timeSlot);
        }
        TimeSlot result = timeSlotRepository.save(timeSlot);
        timeSlotSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("timeSlot", timeSlot.getId().toString()))
            .body(result);
    }

    /**
     * GET  /time-slots : get all the timeSlots.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of timeSlots in body
     */
    @RequestMapping(value = "/time-slots",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TimeSlot> getAllTimeSlots() {
        log.debug("REST request to get all TimeSlots");
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        return timeSlots;
    }

    /**
     * GET  /time-slots/:id : get the "id" timeSlot.
     *
     * @param id the id of the timeSlot to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the timeSlot, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/time-slots/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TimeSlot> getTimeSlot(@PathVariable Long id) {
        log.debug("REST request to get TimeSlot : {}", id);
        TimeSlot timeSlot = timeSlotRepository.findOne(id);
        return Optional.ofNullable(timeSlot)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /time-slots/:id : delete the "id" timeSlot.
     *
     * @param id the id of the timeSlot to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/time-slots/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        log.debug("REST request to delete TimeSlot : {}", id);
        timeSlotRepository.delete(id);
        timeSlotSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("timeSlot", id.toString())).build();
    }

    /**
     * SEARCH  /_search/time-slots?query=:query : search for the timeSlot corresponding
     * to the query.
     *
     * @param query the query of the timeSlot search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/time-slots",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TimeSlot> searchTimeSlots(@RequestParam String query) {
        log.debug("REST request to search TimeSlots for query {}", query);
        return StreamSupport
            .stream(timeSlotSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
