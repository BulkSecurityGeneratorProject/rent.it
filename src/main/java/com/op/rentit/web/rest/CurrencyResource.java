package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.Currency;
import com.op.rentit.repository.CurrencyRepository;
import com.op.rentit.repository.search.CurrencySearchRepository;
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
 * REST controller for managing Currency.
 */
@RestController
@RequestMapping("/api")
public class CurrencyResource {

    private final Logger log = LoggerFactory.getLogger(CurrencyResource.class);
        
    @Inject
    private CurrencyRepository currencyRepository;
    
    @Inject
    private CurrencySearchRepository currencySearchRepository;
    
    /**
     * POST  /currencies : Create a new currency.
     *
     * @param currency the currency to create
     * @return the ResponseEntity with status 201 (Created) and with body the new currency, or with status 400 (Bad Request) if the currency has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/currencies",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Currency> createCurrency(@RequestBody Currency currency) throws URISyntaxException {
        log.debug("REST request to save Currency : {}", currency);
        if (currency.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("currency", "idexists", "A new currency cannot already have an ID")).body(null);
        }
        Currency result = currencyRepository.save(currency);
        currencySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/currencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("currency", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /currencies : Updates an existing currency.
     *
     * @param currency the currency to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated currency,
     * or with status 400 (Bad Request) if the currency is not valid,
     * or with status 500 (Internal Server Error) if the currency couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/currencies",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Currency> updateCurrency(@RequestBody Currency currency) throws URISyntaxException {
        log.debug("REST request to update Currency : {}", currency);
        if (currency.getId() == null) {
            return createCurrency(currency);
        }
        Currency result = currencyRepository.save(currency);
        currencySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("currency", currency.getId().toString()))
            .body(result);
    }

    /**
     * GET  /currencies : get all the currencies.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of currencies in body
     */
    @RequestMapping(value = "/currencies",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Currency> getAllCurrencies() {
        log.debug("REST request to get all Currencies");
        List<Currency> currencies = currencyRepository.findAll();
        return currencies;
    }

    /**
     * GET  /currencies/:id : get the "id" currency.
     *
     * @param id the id of the currency to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the currency, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/currencies/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Currency> getCurrency(@PathVariable Long id) {
        log.debug("REST request to get Currency : {}", id);
        Currency currency = currencyRepository.findOne(id);
        return Optional.ofNullable(currency)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /currencies/:id : delete the "id" currency.
     *
     * @param id the id of the currency to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/currencies/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        log.debug("REST request to delete Currency : {}", id);
        currencyRepository.delete(id);
        currencySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("currency", id.toString())).build();
    }

    /**
     * SEARCH  /_search/currencies?query=:query : search for the currency corresponding
     * to the query.
     *
     * @param query the query of the currency search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/currencies",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Currency> searchCurrencies(@RequestParam String query) {
        log.debug("REST request to search Currencies for query {}", query);
        return StreamSupport
            .stream(currencySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
