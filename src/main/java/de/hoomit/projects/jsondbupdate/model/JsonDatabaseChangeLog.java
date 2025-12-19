package de.hoomit.projects.jsondbupdate.model;

import java.time.ZonedDateTime;

public record JsonDatabaseChangeLog(
        String id,
        String filename,
        ZonedDateTime dateExecuted,
        String md5Sum,
        String description) {}
