package com.op.rentit.config.audit;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditEventConverterTest {

    AuditEventConverter auditEventConverter = new AuditEventConverter();

    @Test
    public void testThatWeCouldConvertDataToStrings() throws Exception {
        Map<String, Object> data = new HashedMap();
        data.put("test", null);
        data.put("test2", new Integer(10));
        WebAuthenticationDetails details = mock(WebAuthenticationDetails.class);
        when(details.getSessionId()).thenReturn("12345");
        data.put("test3", details);
        Map result = auditEventConverter.convertDataToStrings(data);
        assert(result.get("test").equals("null"));
        assert(result.get("test2").equals("10"));
        assert(result.get("sessionId").equals("12345"));
    }

    
//    convertToAuditEvent

}
