package de.hoomit.projects.jsondbupdate.model;

import java.time.ZonedDateTime;

public class JsonDatabaseChangeLog {
    private String id;
    private String filename;
    private ZonedDateTime dateExecuted;
    private String md5Sum;
    private String description;

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

    @Override
    public String toString() {
        return "JsonDatabaseChangeLog{" +
                "id='" + id + '\'' +
                ", filename='" + filename + '\'' +
                ", dateExecuted=" + dateExecuted +
                ", md5Sum='" + md5Sum + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
