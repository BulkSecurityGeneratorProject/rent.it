package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.service.SocialService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class SocialControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SocialService socialService;

    @Mock
    private ProviderSignInUtils providerSignInUtils;

    @Mock
    private OAuth2Connection oAuth2Connection;

    @Inject
    @InjectMocks
    private SocialController socialController;

    @PostConstruct
    public void setup() {
        initMocks(this);
        when(providerSignInUtils.getConnectionFromSession(any())).thenReturn(oAuth2Connection);
        this.mockMvc = MockMvcBuilders.standaloneSetup(socialController).build();
    }

    @Test
    public void testThatWeCouldSignUp() throws Exception {
        when(oAuth2Connection.getKey()).thenReturn(new ConnectionKey("prov","user"));
        mockMvc.perform(get("/social/signup")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isFound());
    }

    @Test
    public void testThatWeCouldHaveErrRedirect() throws Exception {
        mockMvc.perform(get("/social/signup")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isFound());
    }
}
