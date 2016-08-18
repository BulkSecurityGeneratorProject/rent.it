package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.AppSettings;
import com.op.rentit.repository.AppSettingsRepository;
import com.op.rentit.repository.search.AppSettingsSearchRepository;
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
 * REST controller for managing AppSettings.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AppSettingsResource {

    @Inject
    private AppSettingsRepository appSettingsRepository;

    @Inject
    private AppSettingsSearchRepository appSettingsSearchRepository;

    /**
     * POST  /app-settings : Create a new appSettings.
     *
     * @param appSettings the appSettings to create
     * @return the ResponseEntity with status 201 (Created) and with body the new appSettings, or with status 400 (Bad Request) if the appSettings has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/app-settings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AppSettings> createAppSettings(@RequestBody AppSettings appSettings) throws URISyntaxException {
        log.debug("REST request to save AppSettings : {}", appSettings);
        if (appSettings.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appSettings", "idexists", "A new appSettings cannot already have an ID")).body(null);
        }
        AppSettings result = appSettingsRepository.save(appSettings);
        appSettingsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/app-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("appSettings", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /app-settings : Updates an existing appSettings.
     *
     * @param appSettings the appSettings to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated appSettings,
     * or with status 400 (Bad Request) if the appSettings is not valid,
     * or with status 500 (Internal Server Error) if the appSettings couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/app-settings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AppSettings> updateAppSettings(@RequestBody AppSettings appSettings) throws URISyntaxException {
        log.debug("REST request to update AppSettings : {}", appSettings);
        if (appSettings.getId() == null) {
            return createAppSettings(appSettings);
        }
        AppSettings result = appSettingsRepository.save(appSettings);
        appSettingsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("appSettings", appSettings.getId().toString()))
            .body(result);
    }

    /**
     * GET  /app-settings : get all the appSettings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of appSettings in body
     */
    @RequestMapping(value = "/app-settings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AppSettings> getAllAppSettings() {
        log.debug("REST request to get all AppSettings");
        List<AppSettings> appSettings = appSettingsRepository.findAll();
        return appSettings;
    }

    /**
     * GET  /app-settings/:id : get the "id" appSettings.
     *
     * @param id the id of the appSettings to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the appSettings, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/app-settings/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AppSettings> getAppSettings(@PathVariable Long id) {
        log.debug("REST request to get AppSettings : {}", id);
        AppSettings appSettings = appSettingsRepository.findOne(id);
        return Optional.ofNullable(appSettings)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /app-settings/:id : delete the "id" appSettings.
     *
     * @param id the id of the appSettings to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/app-settings/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAppSettings(@PathVariable Long id) {
        log.debug("REST request to delete AppSettings : {}", id);
        appSettingsRepository.delete(id);
        appSettingsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("appSettings", id.toString())).build();
    }

    /**
     * SEARCH  /_search/app-settings?query=:query : search for the appSettings corresponding
     * to the query.
     *
     * @param query the query of the appSettings search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/app-settings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AppSettings> searchAppSettings(@RequestParam String query) {
        log.debug("REST request to search AppSettings for query {}", query);
        return StreamSupport
            .stream(appSettingsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
