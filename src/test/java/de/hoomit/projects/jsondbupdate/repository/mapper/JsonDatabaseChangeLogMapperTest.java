package de.hoomit.projects.jsondbupdate.repository.mapper;

import de.hoomit.projects.jsondbupdate.model.JsonDatabaseChangeLog;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonDatabaseChangeLogMapperTest {

    private final JsonDatabaseChangeLogMapper mapper = new JsonDatabaseChangeLogMapper();

    @Test
    void shouldMapResultSetToRecord() throws SQLException {
        final ZonedDateTime executed = ZonedDateTime.of(2026, 6, 11, 8, 50, 3, 0, ZoneOffset.UTC);
        final ResultSet resultSet = resultSet(Map.of(
                "id", "20260611085003_update_order",
                "filename", "config/jsondbupdate/20260611085003_update_order.csv",
                "date_executed", Timestamp.from(executed.toInstant()),
                "md5sum", "abc123",
                "description", ""
        ));

        final JsonDatabaseChangeLog log = mapper.mapTo(resultSet);

        assertEquals("20260611085003_update_order", log.id());
        assertEquals("config/jsondbupdate/20260611085003_update_order.csv", log.filename());
        assertEquals(executed.toInstant(), log.dateExecuted().toInstant());
        assertEquals("abc123", log.md5Sum());
        assertEquals("", log.description());
    }

    @Test
    void shouldMapNullTimestampToNullDate() throws SQLException {
        final Map<String, Object> values = new HashMap<>();
        values.put("id", "id1");
        values.put("filename", "file1");
        values.put("date_executed", null);
        values.put("md5sum", "sum");
        values.put("description", "desc");

        final JsonDatabaseChangeLog log = mapper.mapTo(resultSet(values));

        assertNull(log.dateExecuted());
    }

    /** Minimal ResultSet stub via dynamic proxy - avoids a mocking dependency. */
    private static ResultSet resultSet(final Map<String, Object> values) {
        final InvocationHandler handler = (proxy, method, args) -> {
            if ("getString".equals(method.getName()) || "getTimestamp".equals(method.getName())) {
                return values.get((String) args[0]);
            }
            throw new UnsupportedOperationException(method.getName());
        };

        return (ResultSet) Proxy.newProxyInstance(
                JsonDatabaseChangeLogMapperTest.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                handler);
    }
}
