package de.hoomit.projects.jsondbupdate.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionUtilsTest {

    @Test
    void shouldReturnTrueForNonEmptyCollection() {
        assertTrue(CollectionUtils.isNotEmpty(List.of("a")));
    }

    @Test
    void shouldReturnFalseForEmptyCollection() {
        assertFalse(CollectionUtils.isNotEmpty(Collections.emptyList()));
    }

    @Test
    void shouldReturnFalseForNull() {
        assertFalse(CollectionUtils.isNotEmpty(null));
    }
}