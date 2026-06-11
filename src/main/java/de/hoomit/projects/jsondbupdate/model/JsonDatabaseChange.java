package de.hoomit.projects.jsondbupdate.model;

public final class JsonDatabaseChange {
    private String action;
    private String entity;
    private String field;
    private String attribute;
    private String newName;
    private String value;

    public Action getAction() {
        return Action.valueOf(action);
    }

    public String getEntity() {
        return entity;
    }

    public String getField() {
        return field;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getNewName() {
        return newName;
    }

    public String getValue() {
        return value;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public void setEntity(final String entity) {
        this.entity = entity;
    }

    public void setField(final String field) {
        this.field = field;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public void setNewName(final String newName) {
        this.newName = newName;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
