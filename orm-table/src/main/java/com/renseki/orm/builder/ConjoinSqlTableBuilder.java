package com.renseki.orm.builder;

import com.renseki.orm.Column;
import com.renseki.orm.SqlTable;
import com.renseki.orm.builder.column.ExistingColumnBuilder;
import com.renseki.orm.exception.MismatchSqlTableJoinException;

import java.util.ArrayList;
import java.util.List;

public class ConjoinSqlTableBuilder extends SqlTable.Builder {

    private final SqlTable src;
    private final SqlTable target;

    public ConjoinSqlTableBuilder(SqlTable src, SqlTable target) {
        this.src = src;
        this.target = target;
    }

    @Override
    public SqlTable build() {
        if ( !src.getTable().equals(target.getTable()) ) {
            throw new MismatchSqlTableJoinException(
                src.getTable(), target.getTable());
        }

        //  Table name
        this.table = src.getTable();
        this.exist = src.isExist();

        //  Columns
        List<Column> columns =
            this.mergeColumns(
                src.getColumns(), target.getColumns());
        this.columns.addAll(columns);

        //  TODO Constraints?

        return new SqlTable(this);
    }

    private List<Column> mergeColumns(List<Column> src, List<Column> target) {
        List<Column> columns = new ArrayList<>();

        List<Column> srcColumns = new ArrayList<>(src);
        List<Column> targetColumns = new ArrayList<>(target);

        //  For All Source
        srcColumns.forEach(srcCol -> {
            final Column column;
            final Column.Mode mode;
            if ( !targetColumns.contains(srcCol) ) {

                //  Jika tidak ada di Model, maka mark as delete
                column = srcCol;
                mode = Column.Mode.DELETE;
            }
            else {
                //  Jika sudah ada, maka dibandingkan
                final int index = targetColumns.indexOf(srcCol);
                Column targetCol = targetColumns.get(index);

                column = targetCol;
                if ( srcCol.isSimilarWith(targetCol) ) {
                    mode = Column.Mode.NOTHING;
                }
                else {
                    mode = Column.Mode.UPDATE;
                }
            }

            //  Add
            columns.add(
                new ExistingColumnBuilder(column)
                    .mode(mode)
                    .build()
            );

            //  Remove All
            targetColumns.remove(srcCol);
        });

        //  Left Over target
        if ( !targetColumns.isEmpty() ) {
            targetColumns
                .forEach(column -> {
                    columns.add(
                        new ExistingColumnBuilder(column)
                            .mode(Column.Mode.NEW)
                            .build()
                    );
                });
        }

        return columns;
    }
}
