package com.renseki.orm.builder.column;

import com.renseki.orm.Column;

public class ExistingColumnBuilder extends Column.Builder {



    public ExistingColumnBuilder(Column column) {
        this.name = column.getName();
        this.type = column.getType();
        this.nullable = column.isNullable();
        this.mode = column.getMode();
        this.primary = column.isPrimary();
        this.relation = column.getRelation();
        this.constraint = column.getConstraint();
        this.pivotTable = column.getPivotTable();
    }

    public ExistingColumnBuilder mode(Column.Mode mode) {
        this.mode = mode;
        return this;
    }

    public ExistingColumnBuilder nullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    @Override
    public Column build() {
        return new Column(this);
    }
}
