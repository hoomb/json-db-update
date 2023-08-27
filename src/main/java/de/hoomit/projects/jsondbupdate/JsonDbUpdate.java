package de.hoomit.projects.jsondbupdate;

import de.hoomit.projects.jsondbupdate.configuration.ApplicationConfiguration;
import de.hoomit.projects.jsondbupdate.service.JsonDbUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonDbUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDbUpdate.class);

    public void startup(final String basePackage, final String dbUrl, final String dbUser, final String dbPassword) {
        LOGGER.info("Starting with JsonDbUpdate process");

        final ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
        applicationConfiguration.init(basePackage, dbUrl, dbUser, dbPassword);

        final JsonDbUpdateService jsonDbUpdateService = new JsonDbUpdateService();
        jsonDbUpdateService.process();

        LOGGER.info("JsonDbUpdate process completed successfully");
    }
}
