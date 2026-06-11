package de.hoomit.projects.jsondbupdate.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceUtilsTest {

    @Test
    void shouldReturnReadableStreamForExistingConfigurationFile() throws IOException {
        try (final InputStream inputStream = ResourceUtils.readConfigurationFile("20260611085003_update_order")) {
            final String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(content.startsWith("action;entity;field"));
            assertTrue(content.contains("REMOVE;Order;extraCosts"));
        }
    }

    @Test
    void shouldReturnNullForMissingConfigurationFile() {
        assertNull(ResourceUtils.readConfigurationFile("does_not_exist"));
    }
}
