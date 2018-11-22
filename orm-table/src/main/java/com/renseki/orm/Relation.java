package com.renseki.orm;

import java.util.Objects;

public class Relation {
    private final String name;
    private final String colRef;
    private final String tableRef;

    public Relation(String name, String colRef, String tableRef) {
        this.name = name;
        this.colRef = colRef;
        this.tableRef = tableRef;
    }

    public String getName() {
        return name;
    }

    public String getColRef() {
        return colRef;
    }

    public String getTableRef() {
        return tableRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relation relation = (Relation) o;
        return Objects.equals(name, relation.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
