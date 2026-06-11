package de.hoomit.projects.jsondbupdate.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FilenameUtilsTest {

    @Test
    void shouldRemoveExtension() {
        assertEquals("20260611085003_update_order", FilenameUtils.removeExtension("20260611085003_update_order.csv"));
    }

    @Test
    void shouldRemoveOnlyLastExtension() {
        assertEquals("archive.tar", FilenameUtils.removeExtension("archive.tar.gz"));
    }

    @Test
    void shouldReturnFilenameWithoutExtensionUnchanged() {
        assertEquals("README", FilenameUtils.removeExtension("README"));
    }

    @Test
    void shouldKeepLeadingDotFiles() {
        assertEquals(".gitignore", FilenameUtils.removeExtension(".gitignore"));
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(FilenameUtils.removeExtension(null));
    }

    @Test
    void shouldReturnEmptyStringUnchanged() {
        assertEquals("", FilenameUtils.removeExtension(""));
    }
}