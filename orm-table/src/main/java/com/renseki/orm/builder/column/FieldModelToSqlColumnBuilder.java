package com.renseki.orm.builder.column;

import com.efitrac.commons.helper.PoolCache;
import com.efitrac.commons.helper.variable.Digit;
import com.efitrac.commons.model.AbstractModel;
import com.efitrac.commons.model.column.Field;
import com.efitrac.commons.model.column.Fields;
import com.google.common.base.Optional;
import com.renseki.orm.Column;
import com.renseki.orm.Relation;
import com.renseki.orm.SqlTable;
import com.renseki.orm.builder.ModelM2MPivotTableBuilder;
import com.renseki.orm.exception.ModelNotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldModelToSqlColumnBuilder extends Column.Builder {

    private static final Map<String, String> MODEL_FIELD_TYPE =
        new HashMap<String, String>(){{
            put("integer", "INT(11)");
            put("manyToOne", "INT(11)");
            put("binary", "INT(11)");
            put("selection", "VARCHAR(255)");
            put("float", "DECIMAL");    //define
            put("text", "LONGTEXT");
            put("char", "VARCHAR(255)");
            put("datetime", "DATETIME");
            put("date", "DATE");
            put("boolean", "TINYINT(1)");
            put("location", "VARCHAR(255)");
            put("manyToMany", "CUSTOM");
        }};

    private final AbstractModel model;
    private final Field field;

    public FieldModelToSqlColumnBuilder(AbstractModel model, Field field) {
        this.model = model;
        this.field = field;
    }

    @Override
    public Column build() {
        if ( !MODEL_FIELD_TYPE.containsKey(field.getType()) ) {
            return null;
        }

        this.mode = Column.Mode.NEW;  //  Column dari Model selalu baru
        this.name = field.getId();
        this.type = this.getColumnType(field);
        this.nullable = !field.isRequired();

        //  Magic Column - [primary] id
        if ( this.name.equals("id") ) {
            this.primary = true;
            this.nullable = false;
        }

        //  Relation - Many To Many
        if ( field instanceof Fields.ManyToOne ) {
            Fields.ManyToOne m2o = (Fields.ManyToOne) field;

            final String fkKey = this.getRelationKey(m2o);
            final String tableTarget = this.getTargetTable(m2o);

            this.relation = new Relation(fkKey, "id", tableTarget);
            this.constraint = fkKey;
        }
        //  Relation - Many To Many
        else if ( field instanceof Fields.ManyToMany ) {
            Fields.ManyToMany m2m = (Fields.ManyToMany) field;
            this.pivotTable = new ModelM2MPivotTableBuilder(model, m2m)
                .build();
        }

        return new Column(this);
    }



    private String getTargetTable(Fields.ManyToOne field) {
        Optional<AbstractModel> optModelTarget =
            PoolCache.getModel(field.getModelRefName());

        if ( !optModelTarget.isPresent() ) {
            throw new ModelNotFoundException(field.getModelRefName());
        }

        AbstractModel modelTarget = optModelTarget.get();
        return modelTarget.getTable();
    }

    private String getRelationKey(Fields.ManyToOne field) {
        Optional<AbstractModel> optModelSrc =
            PoolCache.getModel(field.getModelName());
        Optional<AbstractModel> optModelTarget =
            PoolCache.getModel(field.getModelRefName());

        if ( !optModelSrc.isPresent() ) {
            throw new ModelNotFoundException(field.getModelName());
        }
        if ( !optModelTarget.isPresent() ) {
            throw new ModelNotFoundException(field.getModelRefName());
        }

        AbstractModel modelSrc = optModelSrc.get();
        AbstractModel modelTarget = optModelTarget.get();

        return SqlTable.getRelationKey(
            field.getId(),
            modelSrc.getTable(),
            modelTarget.getTable());
    }

    private String getColumnType(Field field) {
        String res = MODEL_FIELD_TYPE.get(field.getType());
        if ( res.equals("DECIMAL") &&
            field instanceof Fields.Float ) {

            Fields.Float fl = (Fields.Float) field;
            Digit digit = fl.getDigit();

            res += "(" + digit.getPrecision() + "," + digit.getScale() + ")";
        }
        return res;
    }
}
