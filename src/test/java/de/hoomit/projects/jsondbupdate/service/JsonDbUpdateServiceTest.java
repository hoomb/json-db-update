package de.hoomit.projects.jsondbupdate.service;

import de.hoomit.projects.jsondbupdate.model.Action;
import de.hoomit.projects.jsondbupdate.model.JsonDatabaseChange;
import org.junit.jupiter.api.Test;
import org.supercsv.exception.SuperCsvCellProcessorException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDbUpdateServiceTest {

    private final JsonDbUpdateService service = new JsonDbUpdateService();

    @Test
    void shouldReadOrderUpdateFileWithRemoveActions() {
        final Map<Action, List<JsonDatabaseChange>> result = service.readDatabaseChange("20260611085003_update_order");

        assertEquals(1, result.size());

        final List<JsonDatabaseChange> removals = result.get(Action.REMOVE);
        assertEquals(2, removals.size());

        final JsonDatabaseChange first = removals.get(0);
        assertEquals(Action.REMOVE, first.getAction());
        assertEquals("Order", first.getEntity());
        assertEquals("extraCosts", first.getField());
        assertEquals("title", first.getAttribute());
        assertNull(first.getNewName());
        assertNull(first.getValue());

        final JsonDatabaseChange second = removals.get(1);
        assertEquals("subTitle", second.getAttribute());
    }

    @Test
    void shouldReadCustomerUpdateFileAndGroupByAction() {
        final Map<Action, List<JsonDatabaseChange>> result = service.readDatabaseChange("20230825230322_update_customer");

        assertEquals(3, result.size());
        assertTrue(result.containsKey(Action.RENAME));
        assertTrue(result.containsKey(Action.ADD));
        assertTrue(result.containsKey(Action.REMOVE));

        final JsonDatabaseChange rename = result.get(Action.RENAME).get(0);
        assertEquals("Customer", rename.getEntity());
        assertEquals("firstPerson", rename.getField());
        assertEquals("phone", rename.getAttribute());
        assertEquals("phone2", rename.getNewName());

        final JsonDatabaseChange add = result.get(Action.ADD).get(0);
        assertEquals("phone1", add.getAttribute());
        assertNull(add.getNewName());
        assertEquals("test", add.getValue());

        final JsonDatabaseChange remove = result.get(Action.REMOVE).get(0);
        assertEquals("mobile", remove.getAttribute());
    }

    @Test
    void shouldRejectUnknownAction() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.readDatabaseChange("00000000000001_invalid_action"));

        assertTrue(exception.getMessage().contains("DROP"));
    }

    @Test
    void shouldRejectMissingMandatoryColumn() {
        assertThrows(SuperCsvCellProcessorException.class,
                () -> service.readDatabaseChange("00000000000002_missing_entity"));
    }

    @Test
    void shouldNotFailOnEmptyResultForAbsentAction() {
        final Map<Action, List<JsonDatabaseChange>> result = service.readDatabaseChange("20260611085003_update_order");

        assertNull(result.get(Action.ADD));
        assertNull(result.get(Action.RENAME));
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldReturnMutableListOfConfigurationFiles() {
        final List<String> files = service.findAllConfigurationFiles();

        assertFalse(files.isEmpty());
        files.removeAll(List.of(files.get(0)));
    }

    @Test
    void shouldFindConfigurationFilesSortedByTimestamp() {
        final List<String> files = service.findAllConfigurationFiles();

        assertEquals(List.of(
                "00000000000001_invalid_action",
                "00000000000002_missing_entity",
                "20230825230322_update_customer",
                "20260611085003_update_order"), files);
    }
}