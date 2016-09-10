package com.op.rentit.web.rest.mapper;

import com.op.rentit.domain.User;
import com.op.rentit.web.rest.dto.UserDTO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.op.rentit.web.rest.TestUtil.fakeUser;
import static org.junit.Assert.assertTrue;

public class UserMapperImplTest {

    UserMapper userMapper = new UserMapperImpl();

    @Test
    public void userDTOToUser() throws Exception {
        UserDTO userDTO = new UserDTO(fakeUser());
        User user = userMapper.userDTOToUser(userDTO);
        assertTrue(user.getEmail().equals("test@test.com"));
    }

    @Test
    public void userDTOsToUsers() throws Exception {
        List<UserDTO> userDTOs = new ArrayList(
            Arrays.asList(new UserDTO[]{new UserDTO(fakeUser()),
                new UserDTO(fakeUser())}));
        List<User> users = userMapper.userDTOsToUsers(userDTOs);
        assertTrue(users.get(1).getEmail().equals("test@test.com"));
    }

}
