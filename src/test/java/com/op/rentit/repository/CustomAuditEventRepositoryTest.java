package com.op.rentit.repository;

import com.op.rentit.RentitApp;
import com.op.rentit.config.DatabaseConfiguration;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Calendar;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes =  {RentitApp.class, DatabaseConfiguration.class})
@WebIntegrationTest(randomPort = true)
@IntegrationTest
@Transactional
public class CustomAuditEventRepositoryTest {

    @Inject
    private AuditEventRepository customAuditEventRepository;

    @Test
    public void addAndFind() throws Exception {
        AuditEvent auditEvent = new AuditEvent("user","type", new HashedMap());
        customAuditEventRepository.add(auditEvent);
        Calendar date = Calendar.getInstance();
        date.getTime().setTime(100000L);
        customAuditEventRepository.find("user", date.getTime());
    }

}
