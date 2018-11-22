package com.renseki.orm.builder;

import com.efitrac.commons.helper.PoolCache;
import com.efitrac.commons.model.AbstractModel;
import com.efitrac.commons.model.column.Field;
import com.google.common.base.Optional;
import com.renseki.orm.Column;
import com.renseki.orm.SqlTable;
import com.renseki.orm.builder.column.BasicColumnBuilder;
import com.renseki.orm.builder.column.FieldModelToSqlColumnBuilder;
import com.renseki.orm.exception.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelToSqlTableBuilder extends SqlTable.Builder {

    private final String modelName;
    public ModelToSqlTableBuilder(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public SqlTable build() {
        Optional<AbstractModel> optModel = PoolCache.getModel(modelName);
        if ( !optModel.isPresent() ) {
            throw new ModelNotFoundException(modelName);
        }
        AbstractModel model = optModel.get();

        //  Table Name
        this.table = model.getTable();

        //  Fields
        final List<Column> columns = new ArrayList<>();
        Map<String, Field> fields = model.getColumns();
        for ( String fieldName : fields.keySet() ) {
            final Field field = fields.get(fieldName);

            Column column = new FieldModelToSqlColumnBuilder(model, field)
                .build();
            if ( column != null ) {
                columns.add(column);
            }
        }

        //  Add Primary Key
        Column primaryId = new BasicColumnBuilder("id", "INT(11)")
            .nullable(true)
            .primary(true)
            .mode(Column.Mode.NEW)
            .build();
        columns.add(primaryId);

        this.columns.addAll(columns);

        return new SqlTable(this);
    }
}
