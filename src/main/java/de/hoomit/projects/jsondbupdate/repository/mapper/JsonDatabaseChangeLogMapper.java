package de.hoomit.projects.jsondbupdate.repository.mapper;

import de.hoomit.projects.jsondbupdate.model.JsonDatabaseChangeLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class JsonDatabaseChangeLogMapper {

    public JsonDatabaseChangeLog mapTo(final ResultSet resultSet) throws SQLException {
        final JsonDatabaseChangeLog result = new JsonDatabaseChangeLog();

        result.setId(resultSet.getString("id"));
        result.setFilename(resultSet.getString("filename"));
        result.setDateExecuted(fromTimestamp(resultSet, "date_executed"));
        result.setMd5Sum(resultSet.getString("md5sum"));
        result.setDescription(resultSet.getString("description"));

        return result;
    }

    private ZonedDateTime fromTimestamp(final ResultSet resultSet, final String column) throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(column);

        return getDateTime(timestamp);
    }

    private ZonedDateTime getDateTime(final Timestamp timestamp) {
        return timestamp != null ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneOffset.UTC) : null;
    }
}
