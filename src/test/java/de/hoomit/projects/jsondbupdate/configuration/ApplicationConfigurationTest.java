package de.hoomit.projects.jsondbupdate.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ApplicationConfigurationTest {
    @Test
    void shouldBeSingleton() {
        assertSame(ApplicationConfiguration.getInstance(), ApplicationConfiguration.getInstance());
    }

    @Test
    void shouldAppendTrailingDotToBasePackage() {
        final ApplicationConfiguration configuration = ApplicationConfiguration.getInstance();
        configuration.init("de.hoomit.model", "jdbc:postgresql://localhost/db", "user", "pass");

        assertEquals("de.hoomit.model.", configuration.getBasePackage());
        assertEquals("jdbc:postgresql://localhost/db", configuration.getDbUrl());
        assertEquals("user", configuration.getDbUser());
        assertEquals("pass", configuration.getDbPassword());
    }

    @Test
    void shouldNotDuplicateTrailingDot() {
        final ApplicationConfiguration configuration = ApplicationConfiguration.getInstance();
        configuration.init("de.hoomit.model.", "url", "user", "pass");

        assertEquals("de.hoomit.model.", configuration.getBasePackage());
    }
}
