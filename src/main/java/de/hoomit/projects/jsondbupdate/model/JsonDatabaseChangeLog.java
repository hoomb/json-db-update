package de.hoomit.projects.jsondbupdate.model;

import java.time.ZonedDateTime;

public final class JsonDatabaseChangeLog {
    private String id;
    private String filename;
    private ZonedDateTime dateExecuted;
    private String md5Sum;
    private String description;

    public JsonDatabaseChangeLog(final String id, final String filename, final ZonedDateTime dateExecuted, final String md5Sum, final String description) {
        this.id = id;
        this.filename = filename;
        this.dateExecuted = dateExecuted;
        this.md5Sum = md5Sum;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public ZonedDateTime getDateExecuted() {
        return dateExecuted;
    }

    public void setDateExecuted(final ZonedDateTime dateExecuted) {
        this.dateExecuted = dateExecuted;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public void setMd5Sum(final String md5Sum) {
        this.md5Sum = md5Sum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
