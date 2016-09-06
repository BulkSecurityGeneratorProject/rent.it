package com.op.rentit.config.audit;

import com.op.rentit.domain.PersistentAuditEvent;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.time.LocalDateTime;
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

    @Test
    public void testThatWeCouldConvertDataToObjects() throws Exception {
        Map<String, String> data = new HashedMap();
        data.put("test", "null");
        data.put("test2", "10");
        Map result = auditEventConverter.convertDataToObjects(data);
        assertEquals(result.get("test"),"null");
        assertEquals(result.get("test2"),"10");
    }

    @Test
    public void testThatWeCouldConvertToAuditEvent(){
        PersistentAuditEvent event = new PersistentAuditEvent();
        Map<String, String> data = new HashedMap();
        data.put("test", "null");
        event.setData(data);
        event.setPrincipal("admin");
        event.setAuditEventType("T1");
        event.setAuditEventDate(LocalDateTime.now());
        AuditEvent auditEvent = auditEventConverter.convertToAuditEvent(event);
        assertEquals(auditEvent.getPrincipal(),"admin");
    }

    @Test
    public void testThatWeCouldBatchConvertToAuditEvent(){
        PersistentAuditEvent event = new PersistentAuditEvent();
        Map<String, String> data = new HashedMap();
        data.put("test", "null");
        event.setData(data);
        event.setPrincipal("admin");
        event.setAuditEventType("T1");
        event.setAuditEventDate(LocalDateTime.now());
        AuditEvent auditEvent = auditEventConverter.convertToAuditEvent(event);
        assertEquals(auditEvent.getPrincipal(),"admin");
    }
}
