package com.op.rentit.web.rest;

import com.op.rentit.domain.Authority;
import com.op.rentit.domain.User;

import java.util.Arrays;
import java.util.HashSet;

public class TestsHelper {

    public static User fakeUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLangKey("EN");
        user.setLogin("testLogin");
        user.setFirstName("testName");
        Authority authority = new Authority();
        authority.setName("ADMIN");
        user.setAuthorities(new HashSet<Authority>(Arrays.asList(
            new Authority[]{authority}
        )));
        return user;
    }
}
