package com.op.rentit.web.rest;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.User;
import com.op.rentit.repository.UserRepository;
import com.op.rentit.service.MailService;
import com.op.rentit.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebAppConfiguration
@IntegrationTest
public class UserResourceIntTest {

    UserResource userResource = new UserResource();

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    private MockMvc restUserMockMvc;

    @Mock
    private MailService mailService;

    @Before
    public void setup() {
        initMocks(this);
        ReflectionTestUtils.setField(userResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(userResource, "userService", userService);
        ReflectionTestUtils.setField(userResource, "mailService", mailService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
    }

    @Test
    public void testGetExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/admin")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.lastName").value("Administrator"));
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetExistingUserWithAnEmailLogin() throws Exception {
        User user = userService.createUserInformation("john.doe@localhost.com", "johndoe", "John", "Doe", "john.doe@localhost.com", "en-US");

        restUserMockMvc.perform(get("/api/users/john.doe@localhost.com")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.login").value("john.doe@localhost.com"));

        userRepository.delete(user);
    }

    @Test
    public void testDeleteExistingUserWithAnEmailLogin() throws Exception {
        User user = userService.createUserInformation("john.doe@localhost.com", "johndoe", "John", "Doe", "john.doe@localhost.com", "en-US");

        restUserMockMvc.perform(delete("/api/users/john.doe@localhost.com")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(userRepository.findOneByLogin("john.doe@localhost.com").isPresent()).isFalse();

        userRepository.delete(user);
    }

    private void mockAdmin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails user = userDetailsService.loadUserByUsername("admin");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "ADMIN");
        securityContext.setAuthentication(authentication);
    }


    @Test
    public void testThatWeCouldCreateUser() throws Exception {
        User user = userService.createUserInformation("test@test.en", "johndoe", "John", "Doe", "john.doe@localhost.com", "en-US");
        mockAdmin();
        //ManagedUserDTO user = new ManagedUserDTO(fakeUser());
        //ObjectMapper mapper = new ObjectMapper();
        String jsonInString =
            "{\"password\": \"1234\", \"login\": \"aaaaa\", " +
                "\"email\": \"test@test.en\", \"langKey\": \"en\"}";
        //mapper.writeValueAsString(user);

        restUserMockMvc.perform(post("/api/users")
            .accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON)
            .content(jsonInString))
            .andExpect(status().isCreated());

        Optional<User> op = userRepository.findOneByLogin("test@test.en");
        assertThat(op.isPresent()).isTrue();

        userRepository.delete(user);
    }


}
