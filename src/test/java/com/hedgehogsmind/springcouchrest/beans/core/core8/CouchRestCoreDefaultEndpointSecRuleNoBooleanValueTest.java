package com.hedgehogsmind.springcouchrest.beans.core.core8;

import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.beans.exceptions.DefaultSecurityRuleDoesNotReturnBooleanValueException;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfigurationAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import java.util.Optional;

@DataJpaTest
public class CouchRestCoreDefaultEndpointSecRuleNoBooleanValueTest {

    @SpringBootApplication(exclude = {CouchRestCore.class, CouchRestConfiguration.class})
    public static class Config {
        @Bean
        public CouchRestConfiguration config() {
            return new CouchRestConfigurationAdapter() {
                @Override
                public String getDefaultEndpointSecurityRule() {
                    return "'Yet an other test!'";
                }
            };
        }
    }

    @Autowired
    public ApplicationContext applicationContext;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void testExceptionThrownIfEntityAlreadyManagedByCouchRestRepo() {
        Assertions.assertThrows(
                DefaultSecurityRuleDoesNotReturnBooleanValueException.class,
                () -> {
                    final CouchRestCore couchRestCore = new CouchRestCore(applicationContext, entityManager, Optional.empty());
                    couchRestCore.setup();
                }
        );
    }

}
