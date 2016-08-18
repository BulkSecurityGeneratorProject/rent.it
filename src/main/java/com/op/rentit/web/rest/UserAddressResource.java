package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.UserAddress;
import com.op.rentit.repository.UserAddressRepository;
import com.op.rentit.repository.search.UserAddressSearchRepository;
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
 * REST controller for managing UserAddress.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UserAddressResource {

    @Inject
    private UserAddressRepository userAddressRepository;

    @Inject
    private UserAddressSearchRepository userAddressSearchRepository;

    /**
     * POST  /user-addresses : Create a new userAddress.
     *
     * @param userAddress the userAddress to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userAddress, or with status 400 (Bad Request) if the userAddress has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-addresses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserAddress> createUserAddress(@RequestBody UserAddress userAddress) throws URISyntaxException {
        log.debug("REST request to save UserAddress : {}", userAddress);
        if (userAddress.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userAddress", "idexists", "A new userAddress cannot already have an ID")).body(null);
        }
        UserAddress result = userAddressRepository.save(userAddress);
        userAddressSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/user-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("userAddress", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-addresses : Updates an existing userAddress.
     *
     * @param userAddress the userAddress to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userAddress,
     * or with status 400 (Bad Request) if the userAddress is not valid,
     * or with status 500 (Internal Server Error) if the userAddress couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-addresses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserAddress> updateUserAddress(@RequestBody UserAddress userAddress) throws URISyntaxException {
        log.debug("REST request to update UserAddress : {}", userAddress);
        if (userAddress.getId() == null) {
            return createUserAddress(userAddress);
        }
        UserAddress result = userAddressRepository.save(userAddress);
        userAddressSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("userAddress", userAddress.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-addresses : get all the userAddresses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of userAddresses in body
     */
    @RequestMapping(value = "/user-addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<UserAddress> getAllUserAddresses() {
        log.debug("REST request to get all UserAddresses");
        List<UserAddress> userAddresses = userAddressRepository.findAll();
        return userAddresses;
    }

    /**
     * GET  /user-addresses/:id : get the "id" userAddress.
     *
     * @param id the id of the userAddress to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userAddress, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/user-addresses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserAddress> getUserAddress(@PathVariable Long id) {
        log.debug("REST request to get UserAddress : {}", id);
        UserAddress userAddress = userAddressRepository.findOne(id);
        return Optional.ofNullable(userAddress)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-addresses/:id : delete the "id" userAddress.
     *
     * @param id the id of the userAddress to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/user-addresses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long id) {
        log.debug("REST request to delete UserAddress : {}", id);
        userAddressRepository.delete(id);
        userAddressSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userAddress", id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-addresses?query=:query : search for the userAddress corresponding
     * to the query.
     *
     * @param query the query of the userAddress search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/user-addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<UserAddress> searchUserAddresses(@RequestParam String query) {
        log.debug("REST request to search UserAddresses for query {}", query);
        return StreamSupport
            .stream(userAddressSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
