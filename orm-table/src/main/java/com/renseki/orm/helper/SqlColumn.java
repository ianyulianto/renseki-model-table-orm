package com.renseki.orm.helper;

public class SqlColumn {

    private String columnName;
    private String constraintName;
    private String referencedTableName;
    private String referencedColumnName;
    private String isNullable;
    private String columnType;

    public String getColumnName() {
        return columnName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public String getColumnType() {
        return columnType;
    }
}
