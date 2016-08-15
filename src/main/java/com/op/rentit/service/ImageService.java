package com.op.rentit.service;

import com.op.rentit.domain.Image;
import com.op.rentit.repository.ImageRepository;
import com.op.rentit.repository.search.ImageSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Image.
 */
@Service
@Transactional
public class ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageService.class);
    
    @Inject
    private ImageRepository imageRepository;
    
    @Inject
    private ImageSearchRepository imageSearchRepository;
    
    /**
     * Save a image.
     * 
     * @param image the entity to save
     * @return the persisted entity
     */
    public Image save(Image image) {
        log.debug("Request to save Image : {}", image);
        Image result = imageRepository.save(image);
        imageSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the images.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Image> findAll(Pageable pageable) {
        log.debug("Request to get all Images");
        Page<Image> result = imageRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one image by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Image findOne(Long id) {
        log.debug("Request to get Image : {}", id);
        Image image = imageRepository.findOne(id);
        return image;
    }

    /**
     *  Delete the  image by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Image : {}", id);
        imageRepository.delete(id);
        imageSearchRepository.delete(id);
    }

    /**
     * Search for the image corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Image> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Images for query {}", query);
        return imageSearchRepository.search(queryStringQuery(query), pageable);
    }
}
