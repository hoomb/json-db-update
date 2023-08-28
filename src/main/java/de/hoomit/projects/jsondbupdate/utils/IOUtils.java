package de.hoomit.projects.jsondbupdate.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static byte[] toByteArray(final InputStream inputStream) {
        try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            final byte[] data = new byte[512];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
