package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.Image;
import com.op.rentit.service.ImageService;
import com.op.rentit.web.rest.util.HeaderUtil;
import com.op.rentit.web.rest.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class ImageResource {

    public static final String ROOT = "upload-dir";

    private final ResourceLoader resourceLoader;

    @Inject
    private ImageService imageService;

    @Autowired
    public ImageResource(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/imgload")
    @Timed
    public String provideUploadInfo(Model model) throws IOException {

        model.addAttribute("files", Files.walk(Paths.get(ROOT))
            .filter(path -> !path.equals(Paths.get(ROOT)))
            .map(path -> Paths.get(ROOT).relativize(path))
            .collect(Collectors.toList()));

        return "uploadForm";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/imgload/{filename:.+}")
    @ResponseBody
    @Timed
    public ResponseEntity<?> getFile(@PathVariable String filename) {

        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * POST  /images : Create a new image.
     *
     * @param image the image to create
     * @return the ResponseEntity with status 201 (Created) and with body the new image, or with status 400 (Bad Request) if the image has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Image> createImage(@RequestBody Image image) throws URISyntaxException {
        log.debug("REST request to save Image : {}", image);
        if (image.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "idexists", "A new image cannot already have an ID")).body(null);
        }
        Image result = imageService.save(image);
        return ResponseEntity.created(new URI("/api/images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("image", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /images : Updates an existing image.
     *
     * @param image the image to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated image,
     * or with status 400 (Bad Request) if the image is not valid,
     * or with status 500 (Internal Server Error) if the image couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Image> updateImage(@RequestBody Image image) throws URISyntaxException {
        log.debug("REST request to update Image : {}", image);
        if (image.getId() == null) {
            return createImage(image);
        }
        Image result = imageService.save(image);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("image", image.getId().toString()))
            .body(result);
    }

    /**
     * GET  /images : get all the images.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of images in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Image>> getAllImages(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Images");
        Page<Image> page = imageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/images");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /images/:id : get the "id" image.
     *
     * @param id the id of the image to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the image, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/images/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Image> getImage(@PathVariable Long id) {
        log.debug("REST request to get Image : {}", id);
        Image image = imageService.findOne(id);
        return Optional.ofNullable(image)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /images/:id : delete the "id" image.
     *
     * @param id the id of the image to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/images/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        log.debug("REST request to delete Image : {}", id);
        imageService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("image", id.toString())).build();
    }

    /**
     * SEARCH  /_search/images?query=:query : search for the image corresponding
     * to the query.
     *
     * @param query the query of the image search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/images",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Image>> searchImages(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Images for query {}", query);
        Page<Image> page = imageService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/images");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
