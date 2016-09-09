package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.config.DatabaseConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "com.op.rentit")
@SpringApplicationConfiguration(classes = {RentitApp.class, DatabaseConfiguration.class})
@WebAppConfiguration
@IntegrationTest
public class LogsResourceTest {

    @Ignore //TODO: fixme. 404
    @Test
    @WithMockUser(roles="ADMIN")
    public void testThatWeCouldReceiveListOfLogs() throws Exception {
        MockitoAnnotations.initMocks(this);

        MockMvcBuilders.standaloneSetup(LogsResource.class).build()
            .perform(get("/management/jhipster/logs").header("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTQ3NTkyMjY5OH0.xzoDL1D_Y4z2sjz48u4w3zRBb3k6yT0jKkw5UZ9SiW7mGW8SpDBqYMkKcf3_lCetrnZiKsGLnQVHMRxwEwYULQ"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//            .andExpect(jsonPath("$.[*].id").value(hasItem(currency.getId().intValue())))

    }

}
