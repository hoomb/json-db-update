package de.hoomit.projects.jsondbupdate.repository;

import de.hoomit.projects.jsondbupdate.configuration.ApplicationConfiguration;
import de.hoomit.projects.jsondbupdate.model.JsonDatabaseChangeLog;
import de.hoomit.projects.jsondbupdate.repository.mapper.JsonDatabaseChangeLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JsonDbUpdateRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDbUpdateRepository.class);

    private static final String JSON_DATABASE_CHANGE_LOG_TABLE = "json_database_change_log";
    private static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + JSON_DATABASE_CHANGE_LOG_TABLE + " ( " +
            " id varchar(255) NOT NULL, " +
            " filename varchar(255) NOT NULL, " +
            " date_executed timestamp NULL, " +
            " md5sum varchar(255) NOT NULL, " +
            " description varchar(255) NULL, " +
            " CONSTRAINT " + JSON_DATABASE_CHANGE_LOG_TABLE + "_pkey PRIMARY KEY (id) " +
            ");";
    private static final String INSERT_STATEMENT = "INSERT INTO " + JSON_DATABASE_CHANGE_LOG_TABLE +
            " (id, filename, date_executed, md5sum, description) " +
            " VALUES(?, ?, ?, ?, ?);";
    private static final String FIND_ALL_UPDATES_BY_ID_QUERY = "SELECT * FROM " + JSON_DATABASE_CHANGE_LOG_TABLE + " WHERE id = ANY (?)";
    private static final String RENAME_FIELD_QUERY = "UPDATE %s SET %s = (REPLACE(%s::TEXT, '\"%s\"', '\"%s\"'))::JSONB";
    private static final String ADD_FIELD_QUERY = "UPDATE %s SET %s = %s::JSONB || '{\"%s\": null}'";
    private static final String DELETE_FIELD_QUERY = "UPDATE %s SET %s = %s::JSONB - '%s'";

    public Connection createConnection() throws SQLException {
        final ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();

        return DriverManager.getConnection(applicationConfiguration.getDbUrl(), applicationConfiguration.getDbUser(), applicationConfiguration.getDbPassword());
    }

    public void createTable() {
        try (final Connection connection = createConnection();
             final Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_STATEMENT);
        } catch (SQLException e) {
            LOGGER.error("Could not create table", e);
            throw new RuntimeException(e);
        }
    }

    public List<JsonDatabaseChangeLog> findAllUpdatesById(final List<String> configurationFiles) {
        try (final Connection connection = createConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_UPDATES_BY_ID_QUERY)) {
            final Array arrayOfIds = connection.createArrayOf("VARCHAR", configurationFiles.toArray(new String[0]));
            preparedStatement.setArray(1, arrayOfIds);

            final ResultSet resultSet = preparedStatement.executeQuery();

            final JsonDatabaseChangeLogMapper mapper = new JsonDatabaseChangeLogMapper();
            final List<JsonDatabaseChangeLog> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(mapper.mapTo(resultSet));
            }

            return result;
        } catch (SQLException e) {
            LOGGER.error("Could not execute findAllUpdatesById", e);
            throw new RuntimeException(e);
        }
    }

    public void renameField(final String tableName, final String fieldName, final String attribute, final String newName) {
        final String renameFieldQuery = String.format(RENAME_FIELD_QUERY, tableName, fieldName, fieldName, attribute, newName);

        try (final Connection connection = createConnection();
             final Statement statement = connection.createStatement()) {

            statement.executeUpdate(renameFieldQuery);
        } catch (SQLException e) {
            LOGGER.error("Could not rename field {} from Table {} from {} to {}", fieldName, tableName, attribute, newName, e);
            throw new RuntimeException(e);
        }
    }

    public void addField(final String tableName, final String fieldName, final String attribute) {
        final String addFieldQuery = String.format(ADD_FIELD_QUERY, tableName, fieldName, fieldName, attribute);

        try (final Connection connection = createConnection();
             final Statement statement = connection.createStatement()) {

            statement.executeUpdate(addFieldQuery);
        } catch (SQLException e) {
            LOGGER.error("Could not add attribute {} to field {} from Table {}", attribute, fieldName, tableName, e);
            throw new RuntimeException(e);
        }
    }

    public void removeField(final String tableName, final String fieldName, final String attribute) {
        final String deleteFieldQuery = String.format(DELETE_FIELD_QUERY, tableName, fieldName, fieldName, attribute);

        try (final Connection connection = createConnection();
             final Statement statement = connection.createStatement()) {

            statement.executeUpdate(deleteFieldQuery);
        } catch (SQLException e) {
            LOGGER.error("Could not delete attribute {} from field {} from Table {}", attribute, fieldName, tableName, e);
            throw new RuntimeException(e);
        }
    }

    public void persistConfiguration(final JsonDatabaseChangeLog jsonDatabaseChangeLog) {
        try (final Connection connection = createConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT)) {

            preparedStatement.setString(1, jsonDatabaseChangeLog.getId());
            preparedStatement.setString(2, jsonDatabaseChangeLog.getFilename());
            preparedStatement.setTimestamp(3, Timestamp.from(jsonDatabaseChangeLog.getDateExecuted().toInstant()));
            preparedStatement.setString(4, jsonDatabaseChangeLog.getMd5Sum());
            preparedStatement.setString(5, jsonDatabaseChangeLog.getDescription());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Could not persistConfiguration {}", jsonDatabaseChangeLog, e);
            throw new RuntimeException(e);
        }
    }
}
