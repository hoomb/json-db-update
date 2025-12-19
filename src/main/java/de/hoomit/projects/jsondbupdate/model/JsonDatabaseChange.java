package de.hoomit.projects.jsondbupdate.model;

public record JsonDatabaseChange(
        Action action,
        String entity,
        String field,
        String attribute,
        String newName,
        String value) {}
