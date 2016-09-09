package com.op.rentit.service;

import com.op.rentit.RentitApp;
import com.op.rentit.domain.User;
import com.op.rentit.repository.UserRepository;
import com.op.rentit.security.SecurityUtils;
import com.op.rentit.service.util.RandomUtil;
import com.op.rentit.web.rest.dto.ManagedUserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApp.class)
@WebIntegrationTest(randomPort = true)
@IntegrationTest
@Transactional
public class UserServiceIntTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private UserDetailsService userDetailsService;

    @Before
    public void before(){
        mockAdmin();
    }

    @Test
    public void assertThatUserMustExistToResetPassword() {
        Optional<User> maybeUser = userService.requestPasswordReset("john.doe@localhost");
        assertThat(maybeUser.isPresent()).isFalse();

        maybeUser = userService.requestPasswordReset("admin@localhost");
        assertThat(maybeUser.isPresent()).isTrue();

        assertThat(maybeUser.get().getEmail()).isEqualTo("admin@localhost");
        assertThat(maybeUser.get().getResetDate()).isNotNull();
        assertThat(maybeUser.get().getResetKey()).isNotNull();
    }

    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", "en-US");
        Optional<User> maybeUser = userService.requestPasswordReset("john.doe@localhost");
        assertThat(maybeUser.isPresent()).isFalse();
        userRepository.delete(user);
    }

    @Test
    public void assertThatResetKeyMustNotBeOlderThan24Hours() {
        User user = fillNewUser();

        userRepository.save(user);

        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());

        assertThat(maybeUser.isPresent()).isFalse();

        userRepository.delete(user);
    }

    private User fillNewUser() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", "en-US");
        ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(25);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        return user;
    }

    @Test
    public void assertThatResetKeyMustBeValid() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", "en-US");

        ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(25);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");
        userRepository.save(user);
        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(maybeUser.isPresent()).isFalse();
        userRepository.delete(user);
    }

    @Test
    public void assertThatUserCanResetPassword() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", "en-US");
        String oldPassword = user.getPassword();
        ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(2);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userRepository.save(user);
        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(maybeUser.isPresent()).isTrue();
        assertThat(maybeUser.get().getResetDate()).isNull();
        assertThat(maybeUser.get().getResetKey()).isNull();
        assertThat(maybeUser.get().getPassword()).isNotEqualTo(oldPassword);

        userRepository.delete(user);
    }

    @Test
    public void testFindNotActivatedUsersByCreationDateBefore() {

        ZonedDateTime now = ZonedDateTime.now();

        User user = fillNewUser();
        user.setActivated(false);
        user.setCreatedDate(now.minusDays(5));

        userRepository.save(user);

        userService.removeNotActivatedUsers();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        assertThat(users).isEmpty();
    }

    @Test
    public void testThatWeCouldGetUserWithAuthorities() {
        mockAdmin();

        User userWithAuthorities = userService.getUserWithAuthorities();
        assertThat(userWithAuthorities.getLogin().equals("admin"));
        assertThat(userWithAuthorities.getAuthorities().size() > 0);
    }

    @Test
    public void testThatWeCouldGetUserByIdWithAuthorities() {
        User userWithAuthorities = userService.getUserWithAuthorities(1L);
        assertThat(userWithAuthorities.getLogin().equals("admin"));
        assertThat(userWithAuthorities.getAuthorities().size() > 0);
    }

    @Test
    public void testThatWeCouldChangeUserPassword() {
        userService.changePassword("newPassword");
        Optional userOpt = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        assertThat(userOpt.isPresent());
        User userChanged = (User) userOpt.get();
        assertThat(userChanged.getPassword().equals("newPassword"));
    }

    private void mockAdmin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails user = userDetailsService.loadUserByUsername("admin");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "ADMIN");
        securityContext.setAuthentication(authentication);
    }

    @Test
    public void testThatWeCouldCreateUser() {
        User user = new User();
        user.setLogin("testuser");
        user.setActivated(true);
        user.setEmail("test@aaa.test");
        user.setFirstName("newName");
        user.setLastName("newLastname");
        ManagedUserDTO managedUserDTO = new ManagedUserDTO(user);
        managedUserDTO.setLastModifiedBy("bbb");
        User newUser = userService.createUser(managedUserDTO);

        User foundUser = userRepository.findOne(newUser.getId());
        assertThat(foundUser.getFirstName().equals("newName"));
        assertThat(foundUser.getLastName().equals("newLastname"));
        assertThat(foundUser.getEmail().equals("test@aaa.test"));
    }

    @Test
    public void testThatWeCouldActivateUser(){

        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", "en-US");
        Optional<User> activated = userService.activateRegistration(user.getActivationKey());
        User activatedUser = activated.get();
        assertThat(activatedUser.getActivated());
    }

    @Test
    public void testThatWeCouldUpdateUserInfo() {
        userService.updateUserInformation("newName", "newLastname", "test@aaa.test", "EN");
        Optional<User> userOpt = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        assertThat(userOpt.isPresent());
        User userChanged = userOpt.get();
        assertThat(userChanged.getFirstName().equals("newName"));
        assertThat(userChanged.getLastName().equals("newLastname"));
        assertThat(userChanged.getEmail().equals("test@aaa.test"));
    }

}
