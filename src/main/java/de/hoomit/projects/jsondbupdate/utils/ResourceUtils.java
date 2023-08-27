package de.hoomit.projects.jsondbupdate.utils;

import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {

    public static InputStream getResourceAsStream(String resource) {
        try (final InputStream inputStream = getContextClassLoader().getResourceAsStream(resource)) {

            return inputStream == null ? ResourceUtils.class.getResourceAsStream(resource) : inputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
