package de.hoomit.projects.jsondbupdate.service;

import de.hoomit.projects.jsondbupdate.configuration.ApplicationConfiguration;
import de.hoomit.projects.jsondbupdate.model.Action;
import de.hoomit.projects.jsondbupdate.model.JsonDatabaseChange;
import de.hoomit.projects.jsondbupdate.model.JsonDatabaseChangeLog;
import de.hoomit.projects.jsondbupdate.repository.JsonDbUpdateRepository;
import de.hoomit.projects.jsondbupdate.utils.CollectionUtils;
import de.hoomit.projects.jsondbupdate.utils.FilenameUtils;
import de.hoomit.projects.jsondbupdate.utils.ResourceUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class JsonDbUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDbUpdateService.class);
    public static final String CONFIG_FOLDER = "config/jsondbupdate";

    private final JsonDbUpdateRepository jsonDbUpdateRepository;
    private final ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();

    public JsonDbUpdateService() {
        this.jsonDbUpdateRepository = new JsonDbUpdateRepository();
    }

    public void process() {
        jsonDbUpdateRepository.createTable();

        final List<String> configurationFiles = findAllConfigurationFiles();
        final List<JsonDatabaseChangeLog> allApplied = jsonDbUpdateRepository.findAllUpdatesById(configurationFiles);

        if (CollectionUtils.isNotEmpty(allApplied)) {
            configurationFiles.retainAll(allApplied.stream().map(JsonDatabaseChangeLog::getId).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(configurationFiles)) {
            configurationFiles.forEach(this::processDatabaseChange);
        }
    }

    private void processDatabaseChange(final String configurationFile) {
        LOGGER.info("processing json update file {}", configurationFile);

        final Map<Action, List<JsonDatabaseChange>> actionListMap = readDatabaseChange(configurationFile);

        renameFields(actionListMap);

        removeFields(actionListMap);

        addFields(actionListMap);

        persistConfiguration(configurationFile);
    }

    private void addFields(final Map<Action, List<JsonDatabaseChange>> actionListMap) {
        final List<JsonDatabaseChange> addFieldActions = actionListMap.get(Action.ADD);
        if (CollectionUtils.isNotEmpty(addFieldActions)) {
            addFieldActions.forEach(jsonDatabaseChange -> {
                final String tableName = getTableNameFromEntity(jsonDatabaseChange.getEntity());
                final String fieldName = getFieldNameFromEntity(jsonDatabaseChange.getEntity(), jsonDatabaseChange.getField());

                jsonDbUpdateRepository.addField(tableName, fieldName, jsonDatabaseChange.getAttribute());
            });
        }
    }

    private void removeFields(final Map<Action, List<JsonDatabaseChange>> actionListMap) {
        final List<JsonDatabaseChange> removeFieldActions = actionListMap.get(Action.REMOVE);
        if (CollectionUtils.isNotEmpty(removeFieldActions)) {
            removeFieldActions.forEach(jsonDatabaseChange -> {
                final String tableName = getTableNameFromEntity(jsonDatabaseChange.getEntity());
                final String fieldName = getFieldNameFromEntity(jsonDatabaseChange.getEntity(), jsonDatabaseChange.getField());

                jsonDbUpdateRepository.removeField(tableName, fieldName, jsonDatabaseChange.getAttribute());
            });
        }
    }

    private void renameFields(final Map<Action, List<JsonDatabaseChange>> actionListMap) {
        final List<JsonDatabaseChange> renameFieldActions = actionListMap.get(Action.RENAME);
        if (CollectionUtils.isNotEmpty(renameFieldActions)) {
            renameFieldActions.forEach(jsonDatabaseChange -> {
                final String tableName = getTableNameFromEntity(jsonDatabaseChange.getEntity());
                final String fieldName = getFieldNameFromEntity(jsonDatabaseChange.getEntity(), jsonDatabaseChange.getField());

                jsonDbUpdateRepository.renameField(tableName, fieldName, jsonDatabaseChange.getAttribute(), jsonDatabaseChange.getNewName());
            });
        }
    }

    private void persistConfiguration(final String configurationFile) {
        final JsonDatabaseChangeLog jsonDatabaseChangeLog = new JsonDatabaseChangeLog();
        jsonDatabaseChangeLog.setId(configurationFile);
        jsonDatabaseChangeLog.setFilename(CONFIG_FOLDER + "/" + configurationFile + ".csv");
        jsonDatabaseChangeLog.setDescription("");
        jsonDatabaseChangeLog.setDateExecuted(ZonedDateTime.now());
        jsonDatabaseChangeLog.setMd5Sum(calculateMd5Sum(configurationFile));

        jsonDbUpdateRepository.persistConfiguration(jsonDatabaseChangeLog);
    }

    private String calculateMd5Sum(final String configurationFile) {
        try (final InputStreamReader inputStreamReader = readConfigurationFile(configurationFile)) {
            final byte[] content = IOUtils.toByteArray(inputStreamReader, Charset.defaultCharset());
            final byte[] md5Digest = MessageDigest.getInstance("MD5").digest(content);
            return new BigInteger(1, md5Digest).toString(16);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTableNameFromEntity(final String entityName) {
        try {
            final Class<?> entityClass = Class.forName(applicationConfiguration.getBasePackage() + entityName);
            final Table tableAnnotation = entityClass.getAnnotation(Table.class);

            return tableAnnotation.name();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFieldNameFromEntity(final String entityName, final String fieldName) {
        try {
            final Class<?> entityClass = Class.forName(applicationConfiguration.getBasePackage() + entityName);
            final Field field = entityClass.getDeclaredField(fieldName);

            final Column columnAnnotation = field.getAnnotation(Column.class);

            return columnAnnotation != null ? columnAnnotation.name() : fieldName;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Action, List<JsonDatabaseChange>> readDatabaseChange(final String configurationFile) {
        final List<JsonDatabaseChange> changes = new ArrayList<>();

        final CellProcessor[] processors = new CellProcessor[]{
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
        };

        final CsvPreference csvPreference = new CsvPreference.Builder('"', ';', "\n").build();

        try (final InputStreamReader inputStreamReader = readConfigurationFile(configurationFile);
             final CsvBeanReader beanReader = new CsvBeanReader(inputStreamReader, csvPreference)) {
            final String[] header = beanReader.getHeader(true);

            JsonDatabaseChange jsonDatabaseChange;
            while ((jsonDatabaseChange = beanReader.read(JsonDatabaseChange.class, header, processors)) != null) {
                changes.add(jsonDatabaseChange);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return changes.stream()
                .collect(Collectors.groupingBy(JsonDatabaseChange::getAction));
    }

    private InputStreamReader readConfigurationFile(final String configurationFile) {
        final String resourceName = "/" + CONFIG_FOLDER + "/" + configurationFile + ".csv";
        final InputStream inputStream = ResourceUtils.getResourceAsStream(resourceName);

        return new InputStreamReader(inputStream);
    }

    private List<String> findAllConfigurationFiles() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final URL url = loader.getResource(CONFIG_FOLDER);
        final String path = Objects.requireNonNull(url).getPath();

        final File[] csvFiles = new File(path).listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles != null && csvFiles.length > 0) {
            return Arrays.stream(csvFiles)
                    .map(File::getName)
                    .map(FilenameUtils::removeExtension)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
