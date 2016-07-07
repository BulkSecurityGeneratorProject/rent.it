package com.op.rentit.controller;

import com.op.rentit.Application;
import com.op.rentit.model.db.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class LogsRuntimeControllerTest {

    @Autowired
    LogsRuntimeController logsRuntimeController;

    @Ignore
    @Test
    public void testThatWeCouldGetLog() throws Exception {
        System.out.println("hohoho!");
        MockMvcBuilders.standaloneSetup(logsRuntimeController)
                .build().perform(get("/api/logs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //logsRuntimeController.getList();
        User user = new User();
        user.setEmail("aaa");
        user.setId(12L);
    }
}