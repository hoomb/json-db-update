package de.hoomit.projects.jsondbupdate.repository.mapper;

import de.hoomit.projects.jsondbupdate.model.JsonDatabaseChangeLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class JsonDatabaseChangeLogMapper {

    private static final String DATE_EXECUTED_COLUMN = "date_executed";

    public JsonDatabaseChangeLog mapTo(final ResultSet resultSet) throws SQLException {
        return new JsonDatabaseChangeLog(
                resultSet.getString("id"),
                resultSet.getString("filename"),
                fromTimestamp(resultSet),
                resultSet.getString("md5sum"),
                resultSet.getString("description")
        );
    }

    private ZonedDateTime fromTimestamp(final ResultSet resultSet) throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(DATE_EXECUTED_COLUMN);

        return getDateTime(timestamp);
    }

    private ZonedDateTime getDateTime(final Timestamp timestamp) {
        return timestamp != null ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneOffset.UTC) : null;
    }
}
