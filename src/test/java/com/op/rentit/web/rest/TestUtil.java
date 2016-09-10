package com.op.rentit.web.rest;

import com.op.rentit.domain.Authority;
import com.op.rentit.domain.User;
import com.op.rentit.domain.util.JSR310DateTimeSerializer;
import com.op.rentit.domain.util.JSR310LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    /** MediaType for JSON UTF8 */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

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




    /**
     * Convert an object to JSON byte array.
     *
     * @param object
     *            the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(OffsetDateTime.class, JSR310DateTimeSerializer.INSTANCE);
        module.addSerializer(ZonedDateTime.class, JSR310DateTimeSerializer.INSTANCE);
        module.addSerializer(LocalDateTime.class, JSR310DateTimeSerializer.INSTANCE);
        module.addSerializer(Instant.class, JSR310DateTimeSerializer.INSTANCE);
        module.addDeserializer(LocalDate.class, JSR310LocalDateDeserializer.INSTANCE);
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }

    /**
     * Create a byte array with a specific size filled with specified data.
     *
     * @param size the size of the byte array
     * @param data the data to put in the byte array
     */
    public static byte[] createByteArray(int size, String data) {
        byte[] byteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = Byte.parseByte(data, 2);
        }
        return byteArray;
    }
}
