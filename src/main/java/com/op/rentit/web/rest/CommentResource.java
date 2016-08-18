package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.Comment;
import com.op.rentit.repository.CommentRepository;
import com.op.rentit.repository.search.CommentSearchRepository;
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
 * REST controller for managing Comment.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CommentResource {

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private CommentSearchRepository commentSearchRepository;

    /**
     * POST  /comments : Create a new comment.
     *
     * @param comment the comment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new comment, or with status 400 (Bad Request) if the comment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/comments",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) throws URISyntaxException {
        log.debug("REST request to save Comment : {}", comment);
        if (comment.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("comment", "idexists", "A new comment cannot already have an ID")).body(null);
        }
        Comment result = commentRepository.save(comment);
        commentSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/comments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("comment", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /comments : Updates an existing comment.
     *
     * @param comment the comment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated comment,
     * or with status 400 (Bad Request) if the comment is not valid,
     * or with status 500 (Internal Server Error) if the comment couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/comments",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Comment> updateComment(@RequestBody Comment comment) throws URISyntaxException {
        log.debug("REST request to update Comment : {}", comment);
        if (comment.getId() == null) {
            return createComment(comment);
        }
        Comment result = commentRepository.save(comment);
        commentSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("comment", comment.getId().toString()))
            .body(result);
    }

    /**
     * GET  /comments : get all the comments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of comments in body
     */
    @RequestMapping(value = "/comments",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Comment> getAllComments() {
        log.debug("REST request to get all Comments");
        List<Comment> comments = commentRepository.findAll();
        return comments;
    }

    /**
     * GET  /comments/:id : get the "id" comment.
     *
     * @param id the id of the comment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the comment, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/comments/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Comment> getComment(@PathVariable Long id) {
        log.debug("REST request to get Comment : {}", id);
        Comment comment = commentRepository.findOne(id);
        return Optional.ofNullable(comment)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /comments/:id : delete the "id" comment.
     *
     * @param id the id of the comment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/comments/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);
        commentRepository.delete(id);
        commentSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("comment", id.toString())).build();
    }

    /**
     * SEARCH  /_search/comments?query=:query : search for the comment corresponding
     * to the query.
     *
     * @param query the query of the comment search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/comments",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Comment> searchComments(@RequestParam String query) {
        log.debug("REST request to search Comments for query {}", query);
        return StreamSupport
            .stream(commentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
