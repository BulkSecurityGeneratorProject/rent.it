package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.ProductAddress;
import com.op.rentit.repository.ProductAddressRepository;
import com.op.rentit.repository.search.ProductAddressSearchRepository;
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
 * REST controller for managing ProductAddress.
 */
@RestController
@RequestMapping("/api")
public class ProductAddressResource {

    private final Logger log = LoggerFactory.getLogger(ProductAddressResource.class);
        
    @Inject
    private ProductAddressRepository productAddressRepository;
    
    @Inject
    private ProductAddressSearchRepository productAddressSearchRepository;
    
    /**
     * POST  /product-addresses : Create a new productAddress.
     *
     * @param productAddress the productAddress to create
     * @return the ResponseEntity with status 201 (Created) and with body the new productAddress, or with status 400 (Bad Request) if the productAddress has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/product-addresses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProductAddress> createProductAddress(@RequestBody ProductAddress productAddress) throws URISyntaxException {
        log.debug("REST request to save ProductAddress : {}", productAddress);
        if (productAddress.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productAddress", "idexists", "A new productAddress cannot already have an ID")).body(null);
        }
        ProductAddress result = productAddressRepository.save(productAddress);
        productAddressSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/product-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("productAddress", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /product-addresses : Updates an existing productAddress.
     *
     * @param productAddress the productAddress to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated productAddress,
     * or with status 400 (Bad Request) if the productAddress is not valid,
     * or with status 500 (Internal Server Error) if the productAddress couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/product-addresses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProductAddress> updateProductAddress(@RequestBody ProductAddress productAddress) throws URISyntaxException {
        log.debug("REST request to update ProductAddress : {}", productAddress);
        if (productAddress.getId() == null) {
            return createProductAddress(productAddress);
        }
        ProductAddress result = productAddressRepository.save(productAddress);
        productAddressSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("productAddress", productAddress.getId().toString()))
            .body(result);
    }

    /**
     * GET  /product-addresses : get all the productAddresses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of productAddresses in body
     */
    @RequestMapping(value = "/product-addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProductAddress> getAllProductAddresses() {
        log.debug("REST request to get all ProductAddresses");
        List<ProductAddress> productAddresses = productAddressRepository.findAll();
        return productAddresses;
    }

    /**
     * GET  /product-addresses/:id : get the "id" productAddress.
     *
     * @param id the id of the productAddress to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the productAddress, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/product-addresses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProductAddress> getProductAddress(@PathVariable Long id) {
        log.debug("REST request to get ProductAddress : {}", id);
        ProductAddress productAddress = productAddressRepository.findOne(id);
        return Optional.ofNullable(productAddress)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /product-addresses/:id : delete the "id" productAddress.
     *
     * @param id the id of the productAddress to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/product-addresses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteProductAddress(@PathVariable Long id) {
        log.debug("REST request to delete ProductAddress : {}", id);
        productAddressRepository.delete(id);
        productAddressSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("productAddress", id.toString())).build();
    }

    /**
     * SEARCH  /_search/product-addresses?query=:query : search for the productAddress corresponding
     * to the query.
     *
     * @param query the query of the productAddress search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/product-addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProductAddress> searchProductAddresses(@RequestParam String query) {
        log.debug("REST request to search ProductAddresses for query {}", query);
        return StreamSupport
            .stream(productAddressSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
