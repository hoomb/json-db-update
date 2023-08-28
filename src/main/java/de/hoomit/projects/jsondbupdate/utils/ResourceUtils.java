package de.hoomit.projects.jsondbupdate.utils;

import java.io.IOException;
import java.io.InputStream;

import static de.hoomit.projects.jsondbupdate.service.JsonDbUpdateService.CONFIG_FOLDER;

public class ResourceUtils {

    public static InputStream readConfigurationFile(final String configurationFile) {
        final String resourceName = "/" + CONFIG_FOLDER + "/" + configurationFile + ".csv";
        return ResourceUtils.getResourceAsStream(resourceName);

        //return new InputStreamReader(inputStream);
    }

    private static InputStream getResourceAsStream(String resource) {
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
