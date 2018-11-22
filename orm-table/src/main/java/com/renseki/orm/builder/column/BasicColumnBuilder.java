package com.renseki.orm.builder.column;

import com.renseki.orm.Column;
import com.renseki.orm.Relation;

public class BasicColumnBuilder extends Column.Builder {

    public BasicColumnBuilder(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public BasicColumnBuilder nullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public BasicColumnBuilder mode(Column.Mode mode) {
        this.mode = mode;
        return this;
    }

    public BasicColumnBuilder primary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public BasicColumnBuilder relation(Relation relation) {
        this.relation = relation;
        return this;
    }

    public BasicColumnBuilder constraint(String constraint) {
        this.constraint = constraint;
        return this;
    }

    @Override
    public Column build() {
        return new Column(this);
    }
}
