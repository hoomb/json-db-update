package de.hoomit.projects.jsondbupdate.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IOUtilsTest {

    @Test
    void shouldReadAllBytes() {
        final byte[] content = "action;entity;field".getBytes(StandardCharsets.UTF_8);

        assertArrayEquals(content, IOUtils.toByteArray(new ByteArrayInputStream(content)));
    }

    @Test
    void shouldReadContentLargerThanInternalBuffer() {
        final byte[] content = new byte[4096 + 17]; // internal buffer is 512 bytes
        new Random(42).nextBytes(content);

        assertArrayEquals(content, IOUtils.toByteArray(new ByteArrayInputStream(content)));
    }

    @Test
    void shouldReturnEmptyArrayForEmptyStream() {
        assertEquals(0, IOUtils.toByteArray(new ByteArrayInputStream(new byte[0])).length);
    }

    @Test
    void shouldWrapIOExceptionInRuntimeException() {
        final InputStream failing = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };

        assertThrows(RuntimeException.class, () -> IOUtils.toByteArray(failing));
    }
}