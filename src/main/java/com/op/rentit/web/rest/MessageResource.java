package com.op.rentit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.op.rentit.domain.Message;
import com.op.rentit.repository.MessageRepository;
import com.op.rentit.repository.search.MessageSearchRepository;
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
 * REST controller for managing Message.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class MessageResource {

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private MessageSearchRepository messageSearchRepository;

    /**
     * POST  /messages : Create a new message.
     *
     * @param message the message to create
     * @return the ResponseEntity with status 201 (Created) and with body the new message, or with status 400 (Bad Request) if the message has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> createMessage(@RequestBody Message message) throws URISyntaxException {
        log.debug("REST request to save Message : {}", message);
        if (message.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("message", "idexists", "A new message cannot already have an ID")).body(null);
        }
        Message result = messageRepository.save(message);
        messageSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("message", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /messages : Updates an existing message.
     *
     * @param message the message to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated message,
     * or with status 400 (Bad Request) if the message is not valid,
     * or with status 500 (Internal Server Error) if the message couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> updateMessage(@RequestBody Message message) throws URISyntaxException {
        log.debug("REST request to update Message : {}", message);
        if (message.getId() == null) {
            return createMessage(message);
        }
        Message result = messageRepository.save(message);
        messageSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("message", message.getId().toString()))
            .body(result);
    }

    /**
     * GET  /messages : get all the messages.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of messages in body
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Message> getAllMessages() {
        log.debug("REST request to get all Messages");
        List<Message> messages = messageRepository.findAll();
        return messages;
    }

    /**
     * GET  /messages/:id : get the "id" message.
     *
     * @param id the id of the message to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the message, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/messages/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> getMessage(@PathVariable Long id) {
        log.debug("REST request to get Message : {}", id);
        Message message = messageRepository.findOne(id);
        return Optional.ofNullable(message)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /messages/:id : delete the "id" message.
     *
     * @param id the id of the message to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/messages/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.debug("REST request to delete Message : {}", id);
        messageRepository.delete(id);
        messageSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("message", id.toString())).build();
    }

    /**
     * SEARCH  /_search/messages?query=:query : search for the message corresponding
     * to the query.
     *
     * @param query the query of the message search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/messages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Message> searchMessages(@RequestParam String query) {
        log.debug("REST request to search Messages for query {}", query);
        return StreamSupport
            .stream(messageSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
