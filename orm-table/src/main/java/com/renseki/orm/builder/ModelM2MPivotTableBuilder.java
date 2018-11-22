package com.renseki.orm.builder;

import com.efitrac.commons.helper.PoolCache;
import com.efitrac.commons.model.AbstractModel;
import com.efitrac.commons.model.column.Fields;
import com.google.common.base.Optional;
import com.renseki.orm.Column;
import com.renseki.orm.Relation;
import com.renseki.orm.SqlTable;
import com.renseki.orm.builder.column.BasicColumnBuilder;
import com.renseki.orm.exception.ModelNotFoundException;

import java.util.Arrays;
import java.util.List;

public class ModelM2MPivotTableBuilder extends SqlTable.Builder {

    private final AbstractModel model;
    private final Fields.ManyToMany m2m;

    public ModelM2MPivotTableBuilder(AbstractModel model, Fields.ManyToMany m2m) {
        this.model = model;
        this.m2m = m2m;
    }


    private List<Column> getPivotColumns(
        AbstractModel model, Fields.ManyToMany m2m) {

        //  Target Model M2m
        Optional<AbstractModel> optTargetModel_2 = PoolCache.getModel(m2m.getModelRefName());
        if ( !optTargetModel_2.isPresent() ) {
            throw new ModelNotFoundException(m2m.getModelRefName());
        }
        AbstractModel targetModel_2 = optTargetModel_2.get();

        //  Pivot Table Name
        final String pivotTable = m2m.getRelName();

        //  ID
        Column id = new BasicColumnBuilder("id", "INT(11)")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .primary(true)
            .build();

        //  Col Reference 1
        final String targetTable_1 = model.getTable();
        final String targetColumn_1 = m2m.getCurrentFieldRelId();
        final String rel1 = SqlTable.getRelationKey(targetColumn_1, "", targetTable_1);
        Column refCol_1 = new BasicColumnBuilder(targetColumn_1, "INT(11)")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .relation(new Relation(rel1, "id", targetTable_1))
            .build();

        //  Col 2
        final String targetTable_2 = targetModel_2.getTable();
        final String targetColumn_2 = m2m.getTargetFieldRelId();
        final String rel2 = SqlTable.getRelationKey(targetColumn_2, "", targetTable_2);
        Column refCol_2 = new BasicColumnBuilder(targetColumn_2, "INT(11)")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .relation(new Relation(rel2, "id", targetTable_2))
            .build();

        /*
            Create & Write UID
         */
        final String createUidRelKey = SqlTable.getRelationKey("create_uid", "", "res_users");
        Column createUid = new BasicColumnBuilder("create_uid", "INT(11)")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .relation(new Relation(createUidRelKey, "id", "res_users"))
            .build();

        final String writeUidRelKey = SqlTable.getRelationKey("write_uid", "", "res_users");
        Column writeUid = new BasicColumnBuilder("write_uid", "INT(11)")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .relation(new Relation(writeUidRelKey, "id", "res_users"))
            .build();

        /*
            Create & Write Date
         */
        Column createDate = new BasicColumnBuilder("create_date", "DATETIME")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .build();

        Column writeDate = new BasicColumnBuilder("write_date", "DATETIME")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .build();

        /*
            Active & Sys_company_id
         */
        final String companyRelKey = SqlTable.getRelationKey("sys_company_id", "", "res_company");
        Column sysCompanyId = new BasicColumnBuilder("sys_company_id", "INT(11)")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .relation(new Relation(companyRelKey, "sys_company_id", "res_company"))
            .build();

        Column active = new BasicColumnBuilder("active", "TINYINT(1)")
            .nullable(false)
            .mode(Column.Mode.NEW)
            .build();

        return Arrays.asList(
            id, refCol_1, refCol_2, createUid, createDate, writeUid, writeDate, sysCompanyId, active
        );
    }

    @Override
    public SqlTable build() {
        this.table = m2m.getRelName();
        this.columns.addAll(this.getPivotColumns(model, m2m));

        return new SqlTable(this);
    }
}
