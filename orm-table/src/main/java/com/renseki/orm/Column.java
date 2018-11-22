package com.renseki.orm;

import java.util.Objects;

public class Column {

    private final String name;
    private final String type;
    private final boolean nullable;
    private final Mode mode;
    private final boolean primary;
    private final Relation relation;
    private final String constraint;
    private final SqlTable pivotTable;

    public Column(Builder b) {
        this.name = b.name;
        this.type = b.type;
        this.nullable = b.nullable;
        this.mode = b.mode;
        this.primary = b.primary;
        this.relation = b.relation;
        this.constraint = b.constraint;
        this.pivotTable = b.pivotTable;
    }

    public SqlTable getPivotTable() {
        return pivotTable;
    }

    public String getConstraint() {
        return constraint;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isPrimary() {
        return primary;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Relation getRelation() {
        return relation;
    }

    public boolean isSimilarWith(Column column) {
        return this.name.equalsIgnoreCase(column.name)
            && this.type.equalsIgnoreCase(column.type)
            && this.nullable == column.nullable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(name, column.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("%s#%s", name, type);
    }

    public static abstract class Builder {
        protected String name;
        protected String type;
        protected boolean nullable;
        protected Mode mode = Mode.NOTHING;
        protected boolean primary = false;
        protected Relation relation;
        protected String constraint;
        protected SqlTable pivotTable;

        public abstract Column build();
    }

    public enum Mode {
        NEW,
        UPDATE,
        DELETE,
        NOTHING
    }
}
