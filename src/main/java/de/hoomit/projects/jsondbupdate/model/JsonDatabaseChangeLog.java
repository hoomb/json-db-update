package de.hoomit.projects.jsondbupdate.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Immutable value class (was a record before the Java 11 downgrade).
 * Accessor names follow the record convention (id(), filename(), ...) so
 * callers did not have to change.
 */
public final class JsonDatabaseChangeLog {

    private final String id;
    private final String filename;
    private final ZonedDateTime dateExecuted;
    private final String md5Sum;
    private final String description;

    public JsonDatabaseChangeLog(final String id, final String filename, final ZonedDateTime dateExecuted,
                                 final String md5Sum, final String description) {
        this.id = id;
        this.filename = filename;
        this.dateExecuted = dateExecuted;
        this.md5Sum = md5Sum;
        this.description = description;
    }

    public String id() {
        return id;
    }

    public String filename() {
        return filename;
    }

    public ZonedDateTime dateExecuted() {
        return dateExecuted;
    }

    public String md5Sum() {
        return md5Sum;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JsonDatabaseChangeLog that = (JsonDatabaseChangeLog) o;
        return Objects.equals(id, that.id)
                && Objects.equals(filename, that.filename)
                && Objects.equals(dateExecuted, that.dateExecuted)
                && Objects.equals(md5Sum, that.md5Sum)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filename, dateExecuted, md5Sum, description);
    }

    @Override
    public String toString() {
        return "JsonDatabaseChangeLog[id=" + id + ", filename=" + filename + ", dateExecuted=" + dateExecuted
                + ", md5Sum=" + md5Sum + ", description=" + description + "]";
    }
}