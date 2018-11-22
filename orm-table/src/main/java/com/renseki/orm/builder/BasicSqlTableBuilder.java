package com.renseki.orm.builder;

import com.renseki.orm.Column;
import com.renseki.orm.SqlTable;

import java.util.List;

public class BasicSqlTableBuilder extends SqlTable.Builder {

    public BasicSqlTableBuilder(String table, List<Column> columns) {
        this.table = table;
        this.columns = columns;
    }

    @Override
    public SqlTable build() {
        return new SqlTable(this);
    }
}
